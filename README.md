[JGit]: https://eclipse.org/jgit/
[JSch]: http://www.jcraft.com/jsch/
[github-api]: https://github.com/code-check/github-api-scala
[Async HTTP Client]: https://github.com/AsyncHttpClient/async-http-client
[json4s]: http://json4s.org/
[AWS Lambda Java libraries]: https://github.com/aws/aws-lambda-java-libs
[SBT Assembly]: http://github.com/sbt/sbt-assembly
[SBT]: http://scala-sbt.org
[SLF4J]: https://www.slf4j.org/
[Typesafe Scala Logging]: https://github.com/typesafehub/scala-logging
[knobs]: http://verizon.github.io/knobs/
[Typesafe Config]: https://typesafehub.github.io/config/

## Merging pull requests on AWS Lambda with GitHub webhooks

The following Scala code builds a Java JAR file that can run on AWS
Lambda to automatically continuously integrate pull requests for a
GitHub project.

### Overview

A Scala program that is tightly coupled to the following libraries:

- The Java library, [JGit], by Eclipse can clone Git repositories,
checkout branches, merge them and push them.
- The Java library, [JSch], from JCraft provides SSH support for JGit
including support for user and host key verification.
- Use of the GitHub API is with a Scala library, [github-api], by SHUNJI
Konishi of Codecheck.
- Connect to GitHub's API over HTTP with a Java library, [Async HTTP
Client], by Ning.
- JSON serialization with the Scala library, [json4s], by Ivan Porto
Carrero and KAZUHIRO Sera.
- The [AWS Lambda Java libraries] by Amazon AWS provide the ability to
run the JAR in their *serverless* Java 8 runtime.
- JAR produced using the SBT plugin, [SBT Assembly], by Eugene Yokota.
- JVM file system housekeeping provided by the sbt.io Scala library,
from [SBT] team at Lightbend, Inc.
- Configuration file support managed by [knobs] from Verizon and
[Typesafe Config] from Lightbend, Inc.
- Logging provided by [SLF4J] of QoS.ch and [Typesafe Scala Logging] by
Lightbend, Inc.

Steps in detail:

- Receive event from GitHub by way of AWS API Gateway request
- Load config file from `application.conf`
- Verify repo in request is same repo in config file
- Find name of base branch in config file to merge on to
- Use GitHub token in config file to call GitHub API
- Find all pull requests in the repo for the base branch
- Set HEAD commit to all pull requests to pending
- Checkout base branch with Git and SSH
- Use SSH keys and known_hosts specified in config file
- Use SSH keys and known_hosts included in JAR file
- Create integration branch specified in config file with Git
- Merge pull requests on to integration branch with Git
- Force push to GitHub using Git and SSH
- If merge succeeds, merged branches
- If merge fails, don't push, and return unmergable branch
- Notify success or failure with GitHub status API

### Installing

For deploying the JAR see the INSTALLING file for instructions.

### Contributing

For development tasks see the CONTRIBUTING file.

### Warranty

**Buyer beware**: This application will overwrite Git branches.
Should the merge complete successfully, the application does a forced
push to a branch of a remote Git repo.  The branch is the one that is
configured in `application.conf`.  A forced push could result in data
loss.  The SSH ~~deploy~~ user key added to GitHub will have access to
all the repos the user is configured for.  However, by the nature of
private repos in GitHub, write access to a root repo in GitHub will
provide the same access to forked repos.  This is advantageous since
the program requires read-access to forks.  Public repos provide
read-access, by default, ~~without a deploy key~~ for any GitHub user.
Since this program requires write-access to the repo configured in
`application.conf`, the ~~deploy~~ user key will have write-access to
~~forks of private~~ all repos that user has access to.

 The code will verify that the Git repo specified in
`application.conf`, including the branch to monitor for pull requests.

The application as written doesn't catch any exceptions.  There's no
need for the program to recover given it is an internal task that runs
in AWS Lambda, a serverless environment.

The SSH key used to pull from private repos or push to repositories,
is included in the JAR file.  This is a security concern, since the
SSH key will have write privileges to remote Git repositories at
GitHub.

Currently, if there is an error then it results in an exception being
thrown and execution being halted.

Some of the failure conditions that should be caught with a friendly
error message, include

- Unable to read or write to temporary directory on filesystem
- Conf file missing from JAR
- Conf file missing directives
- API Gateway json is malformed
- GitHub json is malformed
- SSH keys missing from the JAR
- SSH hosts key file, `known_hosts`, is missing from JAR
- SSH host key verification fails
- Git client is broken or unavailable
- Git server is broken or unavailable
- A Git remote doesn't exist
- Base branch doesn't exist
- HTTP is broken or unavailable
- GitHub API is broken or unavailable
- Configuring Git remotes fails
- Git fetching fails
- Checking out remote branch fails
- Git merge fails
- Git push to remote fails

### References

- https://developer.github.com/webhooks/
- http://aws.amazon.com/blogs/compute/writing-aws-lambda-functions-in-scala/
- http://aws.amazon.com/blogs/compute/dynamic-github-actions-with-aws-lambda/
- http://eclipse.org/jgit/
- http://github.com/code-check/github-api-scala/
- http://www.jcraft.com/jsch/
- http://github.com/ashawley/aws-lambda-scala-hello-world
