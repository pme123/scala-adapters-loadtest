# scala-adapters-loadtest

A Project to do load-tests with See [Gatling](https://gatling.io)
 on a [scala-adapters](https://pme123.github.io/scala-adapters/) project.

**This is experimental!**

## How to

- Adjust the `Config` object with your server- and job- parameters.
- Run the load tests: `sbt gatling:test`
- Check the Report in the console.
- Check the Report in the Browser: 
  
  `open target/gatling/SIMULATION_NAME-UID/index.html`
