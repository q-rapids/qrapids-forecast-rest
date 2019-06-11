# qrapids-forecast-rest ![](https://img.shields.io/badge/License-Apache2.0-blue.svg)
Wrapper to provide the qrapids-forecast library functionality as RESTful services

This component has been created as a result of the Q-Rapids project funded by the European Union Horizon 2020 Research and Innovation programme under grant agreement No 732253.

## Main Functionality
This component wraps [qrapids-forecast](https://github.com/q-rapids/qrapids-forecast) library in order to access it as a RESTful service.

## Technologies
|Property|Description|
| -------------------- | --------------------|
| Type of component    | RESTful Service     |
| Build                | .war                |
| Programming language | Java                |
| Frameworks           | Spring Boot, Gradle |
| External libraries   | qrapids-forecast    |

## How to build
This is a Gradle project. You can use any IDE that supports Gradle to build it, or alternatively you can use the command line using the Gradle wrapper with the command *__gradlew__* if you don't have Gradle installed on your machine or with the command *__gradle__* if you do, followed by the task *__war__*.

```
# Example: using Gradle wrapper to build with dependencies
cd qrapids-forecast-rest
gradlew war
```
After the build is done the WAR file can be found at the __build/libs__ directory

## How to deploy

Available in the [Wiki](https://github.com/q-rapids/qrapids-forecast-rest/wiki)

## Documentation

You can find the user documentation in the repository  and the technical documentation of the RESTful API [here](https://q-rapids.github.io/qrapids-forecast-rest).

## Contributing

You can find guidelines to contribute to the project in the [Q-Rapids repository](https://github.com/q-rapids/q-rapids/blob/master/CONTRIBUTING.md).

## Licensing

Software licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)
 
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.


## Contact 

For problems regarding this component, please open an issue in the [issues section](https://github.com/q-rapids/qrapids-forecast-rest/issues). You can find some guidelines for submitting issues in the [Q-Rapids repository](https://github.com/q-rapids/q-rapids/blob/master/CONTRIBUTING.md).

For general information, please use the [Q-Rapids project website](http://www.q-rapids.eu/contact).
