import java.util.Date

class Additional(val graphDatabase: GraphDatabase) {

  def runTests(): Unit = {
    val actorName = "Bartłomiej Tonia"
    createActorAndRelation(actorName, "ACTS_IN", "title2")

    updateActor(actorName, new Date(), "Krakow")

    findActorsWithMoviesPlayed(6)

    findAverageAmountOfMoviesPlayed(6)

    findActorsThatAreDirectors(5)

    findMoviesRatedByFriends("maheshksp")

    testIndex("Minnie Driver", "Izabella Miko")
  }

  private def createActorAndRelation(actorName: String, relationName: String, title: String) =
    List(
      AdditionalQueries.createActor(actorName),
      AdditionalQueries.createMovie(title),
      AdditionalQueries.createRelation(actorName, title, relationName)
    ).foreach(x => println(graphDatabase.runCypher(x)))

  private def updateActor(actorName: String, birthDate: Date, birthPlace: String): Unit =
    println(graphDatabase.runCypher(AdditionalQueries.updateActor(actorName, birthDate, birthPlace, new Date())))

  private def findActorsWithMoviesPlayed(moviesPlayed: Int): Unit =
    println(graphDatabase.runCypher(AdditionalQueries.findActorsWithMoviesPlayed(moviesPlayed)))

  private def findAverageAmountOfMoviesPlayed(moviesPlayedMin: Int): Unit =
    println(graphDatabase.runCypher(AdditionalQueries.findAvg(moviesPlayedMin)))

  private def findActorsThatAreDirectors(moviesPlayed: Int): Unit =
    println(graphDatabase.runCypher(AdditionalQueries.findActorsDirectorsWithMoviesPlayed(moviesPlayed)))

  private def findMoviesRatedByFriends(login: String): Unit = {
    println(graphDatabase.runCypher(AdditionalQueries.findMoviesRatedByFriends(login)))
  }

  private def testIndex(actorName: String, secondActorName: String):Unit = {
    println("Without index:")
    graphDatabase.runCypherWithDetails(ProfiledQueries.findActor(actorName))
    graphDatabase.runCypherWithDetails(ProfiledQueries.findShortestPathBetweenActors(actorName, secondActorName))

    println("With indexv:")
    println(graphDatabase.runCypher(ProfiledQueries.createIndexOnActor()))
    graphDatabase.runCypherWithDetails(ProfiledQueries.findActorWithIndex(actorName))
    graphDatabase.runCypherWithDetails(ProfiledQueries.findShortestPathBetweenActors(actorName, secondActorName))

    graphDatabase.runCypher(ProfiledQueries.dropIndexOnActor())
  }

  private object AdditionalQueries{
    def createActor(actorName: String): String = s"CREATE (n: Actor {name: \'$actorName\'}) RETURN n"

    def createMovie(title: String): String = s"CREATE (n: Movie {title: \'$title\'}) RETURN n"

    def createRelation(actorName: String, movieName: String, relationName: String): String =
      s"MATCH (a: Actor), (m: Movie) " +
        s"WHERE a.name = \'$actorName\' and m.title = \'$movieName\' CREATE (a)-[r: $relationName]->(m) RETURN r"

    def updateActor(actorName: String, BirthDate: Date, birthPlace: String, currentDate: Date): String =
      s"MATCH (a: Actor) " +
        s"where a.name = \'$actorName\' " +
        s"SET a.birthplace=\'$birthPlace\', a.birthday=\'$birthPlace\', a.lastModified=\'$currentDate\'"

    def findActorsWithMoviesPlayed(moviesPlayed: Int) =
      s"MATCH (a: Actor)-[:ACTS_IN]->(m: Movie) " +
        s"with a, collect(m) as movies where length(movies) > $moviesPlayed " +
        s"return a.name, length(movies)"

    def findAvg(minMoviesPlayed: Int): String =
      s"MATCH (a: Actor)-[:ACTS_IN]->(m: Movie) " +
        s"with a, collect(m) as movies " +
        s"where length(movies) > $minMoviesPlayed " +
        s"return avg(length(movies)) as average"

    def findActorsDirectorsWithMoviesPlayed(moviesPlayed: Int): String =
      "Match (a:Actor) -[:ACTS_IN]-> (m:Movie) " +
        "With a, count(m) as movies " +
        s"where movies > $moviesPlayed " +
        "With a,movies " +
        "Match (a:Director) -[:DIRECTED]-> (m:Movie) " +
        "With a,movies,count(m.title) as directed, collect(m.title) as directedTitles " +
        "Where directed > 0 " +
        "Return a.name,movies,directed,directedTitles " +
        "Order By movies DESC"

    def findMoviesRatedByFriends(login: String): String =
      s"Match (user:Person{login: \'$login\'}) -[:FRIEND]->(f)-[r:RATED]->(m: Movie) " +
        s"where r.stars > 2 " +
        s"return f.name, m.title, r.stars"



  }

  object ProfiledQueries{
    def findActorWithIndex(actorName: String): String =
      s"PROFILE MATCH (a: Actor) USING INDEX a:Actor(name) where a.name = \'$actorName\' return a"

    def createIndexOnActor(): String = "CREATE INDEX ON :Actor(name)"

    def dropIndexOnActor(): String = "DROP INDEX ON :Actor(name)"

    def findActor(actorName: String): String =
      s"PROFILE MATCH (a: Actor) where a.name = \'$actorName\' return a"

    def findShortestPathBetweenActors(actorNameFrom: String, actorNameTo: String): String =
      s"PROFILE MATCH (a: Actor {name: \'$actorNameFrom\'}), (b: Actor {name: \'$actorNameTo\'}) " +
        s"return shortestPath((a)-[*]-(b)) as shortest_path"

  }
}
