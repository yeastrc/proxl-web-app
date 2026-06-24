# Proxl Web App — Build

Builds `proxl.war` (Gradle 9.5.1, Java 25, Jakarta EE 11 — Servlet 6.1 / JSP 4.0,
Spring 7, Jersey 4.0; runs on Tomcat 11).

## Requirements

- A **JDK 17+** on `PATH` to *run* Gradle. The project compiles to **Java 25**
  via a Gradle toolchain, so a JDK 25 must be available for Gradle to use.
- **Node 20 or 24** for the front-end build (webpack 5 / dart `sass` — no native
  compile, no version pin). See [`front_end/README.md`](front_end/README.md).
- Network access to Maven Central and the published proxl-import-api git Maven
  repo: `https://raw.githubusercontent.com/yeastrc/proxl-import-api/repository/`.

## Build the WAR

1. Make a supported **Node** (20 or 24) active in your terminal (details in `front_end/README.md`):

       nvm use 24
       node -v            # confirm: v20.x or v24.x

2. First time only — install the front-end dependencies:

       ( cd front_end && npm install )

3. Build:

       ./gradlew war

   Output: `build/libs/proxl.war`.

The `war` task automatically runs the front-end build (handlebars + webpack),
which is why Node must be active in the terminal.

## Note

The full repository (all modules) is normally built from the project root via
`ant -f ant__build_all_proxl.xml`, which runs inside the
`ghcr.io/yeastrc/proxl-build-docker` image (Node 24 + JDK 25).
The instructions above are for building this module directly in a terminal.
