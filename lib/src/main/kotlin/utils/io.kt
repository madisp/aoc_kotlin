package utils

import java.io.File
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

object Utils

fun readFile(name: String): String {
  val stream = Utils.javaClass.getResourceAsStream("/$name.txt")
  require (stream != null) {
    "No input @ /$name.txt!"
  }
  return stream.use { it.readBytes().toString(Charsets.UTF_8) }
}

fun readInput(year: Int, day: Int): String {
  val file = File("").resolve(year.toString()).resolve("src").resolve("main").resolve("resources").resolve("day${day}.txt")

  if (!file.exists()) {
    val cookie = readFile("cookie").trim()
    val ua = readFile("user-agent").trim()
    val client = HttpClient.newBuilder()
      .version(HttpClient.Version.HTTP_1_1)
      .followRedirects(HttpClient.Redirect.NORMAL)
      .connectTimeout(Duration.ofSeconds(5))
      .build()

    val request = HttpRequest.newBuilder()
      .GET()
      .uri(URI("https://adventofcode.com/${year}/day/${day}/input"))
      .header("Cookie", cookie)
      .header("User-Agent", ua)
      .build()
    val resp = client.send(request, HttpResponse.BodyHandlers.ofInputStream())

    if (resp.statusCode() >= 400) {
      throw IOException("Request to AoC servers failed, status=${resp.statusCode()}")
    }

    resp.body().use { input ->
      file.outputStream().use { output ->
        input.copyTo(output, bufferSize = 8192)
      }
    }
  }

  require (file.exists()) {
    "Could not fetch input for $year / day $day"
  }

  return file.readText(Charsets.UTF_8)
}

fun badInput(): Nothing {
  throw IllegalArgumentException("bad input")
}
