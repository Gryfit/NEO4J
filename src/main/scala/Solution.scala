class Solution(val graphDatabase: GraphDatabase) {

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

  private def findActorByName(actorName: String) =
    graphDatabase.runCypher("MATCH (p:Actor) WHERE p.name = \'" + actorName + "\' RETURN p")

  private def findMovieByTitleLike(movieName: String) =
    graphDatabase.runCypher("MATCH (m:Movie) where m.title CONTAINS \'" + movieName + " \' RETURN m")

  private def findRatedMoviesForUser(userLogin: String) =
    graphDatabase.runCypher("MATCH (u: User {login : \'" + userLogin + "\'})-[:RATED]->(m: Movie) RETURN m")

  private def findCommonMoviesForActors(actorOne: String, actrorTwo: String) =
    graphDatabase.runCypher(
      "MATCH (f:Actor {name: \'" + actorOne + "\'})-[:ACTS_IN]->" +
        "(m)<-[:ACTS_IN]-(s: Actor {name: \'" + actrorTwo + "\'}) RETURN m"
    )


  private def findMovieRecommendationForUser(userLogin: String) =
    graphDatabase.runCypher("Match (user: Person{login: \'" + userLogin + "\'})-[:RATED]->" +
      "(m: Movie)<-[:RATED]-(user2: User)-[:RATED]->(movies: Movie) return movies.title ")

}
