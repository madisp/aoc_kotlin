import com.google.devtools.ksp.symbol.KSFile
import parser.Node
import parser.Prop
import parser.Type
import parser.TypeType

class TypeBuilder(
  val file: KSFile,
  val type: TypeType,
  val qualifiedName: String,
  val packageName: String? = null,
  // following fields are filled later:
  var pattern: Node? = null,
  val props: MutableMap<String, PropBuilder> = mutableMapOf(),
  val genericArguments: MutableList<TypeBuilder> = mutableListOf(),
) {
  fun build(): Type {
    val name = if (packageName == null) qualifiedName else qualifiedName.substring(packageName.length + 1)
    return Type(
      type = type,
      name = name,
      packageName = packageName,
      pattern = pattern,
      props = props.mapValues { (_, p) -> p.build() },
      genericArguments = genericArguments.map { it.build() },
    )
  }
}

class PropBuilder(
  val type: TypeBuilder,
  var pattern: Node? = null,
) {
  fun build() = Prop(
    type = type.build(),
    pattern = pattern,
  )
}
