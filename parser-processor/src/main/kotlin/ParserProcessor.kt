import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.visitor.KSDefaultVisitor
import parser.TypeType
import parser.generateParser
import parser.parse
import java.io.PrintWriter

class ParserProcessor(
  private val log: KSPLogger,
  private val codeGenerator: CodeGenerator,
) : SymbolProcessor {
  override fun process(resolver: Resolver): List<KSAnnotated> {
    val syms = resolver.getSymbolsWithAnnotation("utils.Parse")

    val types = syms.mapNotNull {
      it.accept(ParserVisitor(log), Unit)
    }

    val byFile = types.groupBy { it.file }

    byFile.forEach { (file, types) ->
      codeGenerator.createNewFile(Dependencies(false, file), file.packageName.asString(), "parse_${file.fileName.removeSuffix(".kt")}").use { out ->
        PrintWriter(out, false, Charsets.UTF_8).use {

          if (file.packageName.asString().isNotBlank()) {
            it.println("package ${file.packageName.asString()}")
            it.println()
          }
          types.forEach { type ->
            it.println(generateParser(type.build()))
            it.println()
          }
        }
      }
    }

    return emptyList()
  }

  inner class ParserVisitor(private val log: KSPLogger) : KSDefaultVisitor<Unit, TypeBuilder?>() {
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit): TypeBuilder? {
      if (classDeclaration.primaryConstructor == null) {
        log.warn("No primary ctor for $classDeclaration, not generating")
        return null
      }

      val file = classDeclaration.containingFile
      if (file == null) {
        log.warn("No containing file for $classDeclaration, not generating")
        return null
      }

      if (Modifier.DATA !in classDeclaration.modifiers) {
        log.warn("@Parse annotation can only be applied to data classes, not generating for $classDeclaration")
        return null
      }

      if (classDeclaration.classKind != ClassKind.CLASS) {
        log.warn("@Parse annotation can only be applied to classes, not generating for $classDeclaration")
        return null
      }


      val primaryCtor = classDeclaration.primaryConstructor
      if (primaryCtor == null) {
        log.warn("No primary ctor for $classDeclaration, not generating")
        return null
      }

      log.info("Processing ${classDeclaration.qualifiedName?.asString()}")

      val tb = TypeBuilder(
        file,
        TypeType.CLASS,
        classDeclaration.qualifiedName?.asString() ?: classDeclaration.simpleName.asString(),
        classDeclaration.packageName.asString().takeIf { it.isNotBlank() },
      )

      classDeclaration.annotations.extractPattern(log) { tb.pattern = parse(it) }

      primaryCtor.parameters.forEach { prop ->
        val propType = prop.type.resolve()
        val decl = propType.declaration

        if (decl !is KSClassDeclaration) {
          throw IllegalStateException("prop $prop is not a class for type $classDeclaration")
        }

        val name = prop.name?.asString() ?: return@forEach
        val pb = PropBuilder(
          TypeBuilder(
            file,
            if (Modifier.ENUM in decl.modifiers) TypeType.ENUM else TypeType.CLASS,
            decl.qualifiedName?.asString() ?: decl.simpleName.asString(),
            decl.packageName.asString().takeIf { it.isNotBlank() },
            genericArguments = propType.arguments.mapNotNull { typeArg ->
              val typeArgType = typeArg.type?.resolve() ?: return@mapNotNull null
              TypeBuilder(
                file,
                if (Modifier.ENUM in typeArgType.declaration.modifiers) TypeType.ENUM else TypeType.CLASS,
                typeArgType.declaration.qualifiedName?.asString() ?: typeArgType.declaration.simpleName.asString(),
                typeArgType.declaration.packageName.asString().takeIf { it.isNotBlank() },
              )
            }.toMutableList(),
          ),
        )

        prop.annotations.extractPattern(log) {
          pb.pattern = parse(it)
        }

        tb.props[name] = pb
      }

      classDeclaration.getDeclaredProperties().forEach { prop ->
        val pb = tb.props[prop.simpleName.asString()] ?: return@forEach

        prop.annotations.extractPattern(log) {
          pb.pattern = parse(it)
        }
      }

      return tb
    }

    override fun defaultHandler(node: KSNode, data: Unit) = null
  }
}

private fun Sequence<KSAnnotation>.extractPattern(log: KSPLogger, act: (String) -> Unit) {
  forEach { anno ->
    if (anno.annotationType.resolve().declaration.qualifiedName?.asString() == "utils.Parse") {
      anno.arguments.first { it.name?.getShortName() == "pattern" }.value?.toString()?.let(act)
    }
  }
}
