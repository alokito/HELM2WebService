HELM2Lambda

* Running "mvn package" should create a jar called "HELM2Lambda-1.0.0-SNAPSHOT.jar" in the target folder 

Deployment

* "cd sh" to change directory to the scripts directory
* "cp awsenv.sh.orig awsenv.sh", and edit awsenv.sh to have the correct settings
* ". ./awsenv.sh" to set your environment variables
* ". ./create-lambda.sh" to upload the packaged labmda function. You can run this same script to update the lambda function if you rebuild it.
* ". ./create-api.sh" to create an API in the api gateway. You should only need to do this once.

However, before you build this module you must build the dependencies.

Problems building from source
HELM2WebService requires org.helm:HELMNotationToolkit:jar:2.0.0-SNAPSHOT, org.helm:cdk-impl-chemtoolkit:jar:1.0.0-SNAPSHOT, org.helm:chemaxon-impl-chemtoolkit:jar:1.0.0-SNAPSHOT

HELM2NotationToolkit requires  chemaxon:MarvinBeans:jar:5.0, com.quattroresearch:helm2parser:jar:1.0.0-SNAPSHOT, org.helm:ChemistryToolkit:jar:1.0.0-SNAPSHOT, org.helm:cdk-impl-chemtoolkit:jar:1.0.0-SNAPSHOT, org.helm:chemaxon-impl-chemtoolkit:jar:1.0.0-SNAPSHOT

-- can install marvinbeans easily

still requires com.quattroresearch:helm2parser:jar:1.0.0-SNAPSHOT, org.helm:ChemistryToolkit:jar:1.0.0-SNAPSHOT, org.helm:cdk-impl-chemtoolkit:jar:1.0.0-SNAPSHOT, org.helm:chemaxon-impl-chemtoolkit:jar:1.0.0-SNAPSHOT

ChemistryToolkitMarvin implements chemaxon-impl-chemtoolkit

requires ChemistryToolkit


still need com.quattroresearch:helm2parser:jar:1.0.0-SNAPSHOT, org.helm:cdk-impl-chemtoolkit:jar:1.0.0-SNAPSHOT

helm2parser provided by HELMNotationParser


cdk-impl-chemtoolkit provided by https://github.com/PistoiaHELM/ChemistryToolkitCDK.git
