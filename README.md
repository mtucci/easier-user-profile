Computation of energy and economic cost profiles on the basis of UML and LQN models. Part of the [EASIER](https://github.com/danieledipompeo/easier/) ecosystem.

Used in:
- Vittorio Cortellessa, Daniele Di Pompeo, Michele Tucci,<br>
  "Exploring sustainable alternatives for the deployment of microservices architectures in the cloud",<br>
  International Conference on Software Architecture (ICSA), 2024.<br>
  [https://doi.org/10.48550/arXiv.2402.11238](https://doi.org/10.48550/arXiv.2402.11238)

### Requirements
- Java 11
- Maven

### Build
```bash
git clone https://github.com/mtucci/easier-user-profile.git
cd easier-user-profile
mvn package
```

### Usage
```bash
java -jar target/easier-user-profile-1.0-SNAPSHOT.jar <UML model> <LQN model> > <output file.csv>
```

### Example
```bash
java -jar target/easier-user-profile-1.0-SNAPSHOT.jar src/test/resources/ttbs/0.uml src/test/resources/ttbs/0.lqxo > output.csv
```
