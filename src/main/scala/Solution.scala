class Solution() {
  private val graphDatabase: GraphDatabase = GraphDatabase.createDatabase()

  def databaseStatistics(): Unit = {
    println(graphDatabase.runCypher("CALL db.labels()"))
    println(graphDatabase.runCypher("CALL db.relationshipTypes()"))
  }

  def runAllTests(): Unit = {
    println(findActorByName("Emma Watson"))
    println(findMovieByTitleLike("Star Wars"))
    println(findRatedMoviesForUser("maheshksp"))
    println(findCommonMoviesForActors("Emma Watson", "Daniel Radcliffe"))
    println(findMovieRecommendationForUser("emileifrem"))
  }

  private def findActorByName(actorName: String) = ???

  private def findMovieByTitleLike(movieName: String) = ???

  private def findRatedMoviesForUser(userLogin: String) = ???

  private def findCommonMoviesForActors(actorOne: String, actrorTwo: String) = ???

  private def findMovieRecommendationForUser(userLogin: String) = ???

}
