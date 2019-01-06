
object Main {
  def main(args: Array[String]): Unit = {
    val graphDatabase = GraphDatabase.createDatabase()
    val solution = new Solution(graphDatabase)
    val additional = new Additional(graphDatabase)
    solution.databaseStatistics()
    solution.runAllTests()
    additional.runTests()
  }
}
