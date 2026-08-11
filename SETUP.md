# Setup and Installation Guide - Java File Encryption and Security System

## Prerequisites

- **Java Development Kit (JDK)**: Version 11 or higher
  - Download from [oracle.com](https://www.oracle.com/java/technologies/downloads/) or [adoptium.net](https://adoptium.net/)
- **Maven**: Version 3.6 or higher
  - Download from [maven.apache.org](https://maven.apache.org/download.cgi)
- **Git**: For version control

## Step 1: Install Java Development Kit (JDK)

### macOS
```bash
brew install openjdk@11
# Verify installation
java -version
javac -version
```

### Windows
1. Download JDK 11+ from [oracle.com](https://www.oracle.com/java/technologies/downloads/)
2. Run the installer
3. Add JAVA_HOME to environment variables:
   - Right-click Computer → Properties
   - Click "Environment Variables"
   - Add new system variable `JAVA_HOME` pointing to JDK installation directory
   - Add `%JAVA_HOME%\bin` to PATH

### Ubuntu/Debian
```bash
sudo apt-get update
sudo apt-get install openjdk-11-jdk
java -version
```

## Step 2: Install Maven

### macOS
```bash
brew install maven
mvn --version
```

### Windows
1. Download Maven from https://maven.apache.org/download.cgi
2. Extract to a folder (e.g., `C:\apache-maven`)
3. Add to environment variables:
   - Add `M2_HOME` pointing to Maven directory
   - Add `%M2_HOME%\bin` to PATH
4. Verify: Open new command prompt and run `mvn --version`

### Ubuntu/Debian
```bash
sudo apt-get install maven
mvn --version
```

## Step 3: Clone and Navigate

```bash
cd /Users/jyn/java-file-encryption
```

## Step 4: Build Project

### Full Build

```bash
# Clean and build
mvn clean package

# This will:
# - Download dependencies
# - Compile Java source code
# - Run all tests
# - Package the application as JAR
```

### Build Without Tests

```bash
mvn clean package -DskipTests
```

## Step 5: Run Application

### Using Maven

```bash
mvn javafx:run
```

### Using JAR File

After building, run:

```bash
java -jar target/java-file-encryption-1.0.0.jar
```

## Step 6: Run Tests

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=AES256EngineTest
mvn test -Dtest=SecurityTest
```

### Run with Coverage Report

```bash
mvn test jacoco:report
# View report at: target/site/jacoco/index.html
```

## Project Structure After Build

```
java-file-encryption/
├── src/
│   ├── main/java/                  # Source code
│   └── test/java/                  # Test code
├── target/                          # Build output directory
│   ├── classes/                     # Compiled classes
│   ├── java-file-encryption-1.0.0.jar  # Executable JAR
│   └── ...
├── logs/                            # Application logs
├── pom.xml                          # Maven configuration
├── README.md
└── SETUP.md
```

## IDE Setup

### IntelliJ IDEA

1. **Open Project**
   - File → Open → Select project directory
   - Choose "Open as Project"

2. **Configure JDK**
   - File → Project Structure → Project
   - Set Project SDK to JDK 11+
   - Click "Apply"

3. **Run Application**
   - Right-click `MainApplication.java` → Run
   - Or: Run → Run 'MainApplication'

4. **Run Tests**
   - Right-click `src/test/java` → Run Tests
   - Or: Run → Run All Tests

### Eclipse

1. **Import Project**
   - File → Import → Existing Maven Projects
   - Select project directory
   - Click "Finish"

2. **Configure JDK**
   - Windows → Preferences → Java → Installed JREs
   - Add JDK 11+ if not present

3. **Run Application**
   - Right-click project → Run As → Java Application
   - Select `MainApplication`

4. **Run Tests**
   - Right-click `src/test/java` → Run As → JUnit Test

### VS Code

1. **Install Extensions**
   - Extension Pack for Java
   - Maven for Java

2. **Open Project**
   - File → Open Folder → Select project directory

3. **Run Application**
   - Terminal → New Terminal
   - Run: `mvn javafx:run`

4. **Run Tests**
   - Ctrl+Shift+P → "Maven: Run Tests"

## Troubleshooting

### Maven Not Found

If `mvn` command not recognized:

```bash
# Check Maven path
echo $PATH

# Add Maven to path manually (temporary)
export PATH=$PATH:/path/to/maven/bin

# Verify
mvn --version
```

### Java Version Mismatch

```bash
# Check Java version
java -version

# If multiple versions installed, set JAVA_HOME
export JAVA_HOME=/path/to/jdk11  # macOS/Linux
set JAVA_HOME=C:\path\to\jdk11   # Windows

# Verify
java -version
```

### Dependency Download Errors

```bash
# Clear Maven cache and rebuild
mvn clean install

# Or manually download dependencies
mvn dependency:resolve

# Check Maven settings
mvn help:describe -Dgoal=dependency:resolve
```

### Test Failures

```bash
# Run tests with verbose output
mvn test -X

# Run single failing test
mvn test -Dtest=TestClassName#testMethodName
```

### Database Connection Issues

1. Ensure SQLite JDBC driver is downloaded
2. Check database file permissions
3. Clear `audit.db` file if corrupted:
   ```bash
   rm audit.db
   mvn test  # This will create a fresh database
   ```

### JavaFX Module Not Found

Ensure pom.xml includes JavaFX dependencies (already configured):

```xml
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>21.0.1</version>
</dependency>
```

Run: `mvn dependency:resolve` to download

## Development Workflow

### Making Changes

1. **Edit source files** in `src/main/java/com/filesecurity/`
2. **Write tests** in `src/test/java/com/filesecurity/`
3. **Build project**: `mvn clean package`
4. **Run tests**: `mvn test`
5. **Test application**: `mvn javafx:run`

### Code Style

- Follow standard Java naming conventions
- Use meaningful variable/method names
- Add JavaDoc comments to public classes/methods
- Keep methods focused on single responsibility

### Adding Dependencies

1. Add to `<dependencies>` section in pom.xml
2. Run: `mvn dependency:resolve`
3. Rebuild: `mvn clean package`

## Performance Tuning

### Increase Heap Memory for Large Files

```bash
java -Xmx2048m -jar target/java-file-encryption-1.0.0.jar
```

### Optimize Compilation

```bash
mvn clean package -T 1C  # Use 1 thread per core
```

## Production Deployment

### Create Executable JAR

```bash
mvn clean package -DskipTests
# Result: target/java-file-encryption-1.0.0.jar
```

### Create Windows Executable

Install launch4j and add to pom.xml for .exe wrapper.

### Create macOS Application Bundle

Use appassembler-maven-plugin to create app bundle.

### Docker Containerization

Create `Dockerfile`:
```dockerfile
FROM openjdk:11-jre
COPY target/java-file-encryption-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build: `docker build -t file-encryption:1.0 .`

## Additional Resources

- [Java Documentation](https://docs.oracle.com/en/java/)
- [Maven Documentation](https://maven.apache.org/guides/)
- [JavaFX Documentation](https://gluonhq.com/products/javafx/)
- [JUnit Documentation](https://junit.org/junit4/)

## Support

For issues during setup:
1. Check that all prerequisites are installed
2. Verify environment variables are set correctly
3. Run `mvn clean install` to refresh dependencies
4. Check logs in `logs/` directory
5. Review test output for diagnostic information

---

**Last Updated**: August 2026
