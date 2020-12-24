pipeline {
  agent none
  stages {
    stage('build') {
      steps {
        sh 'mvn clean install -Dlicense.skip=true'
      }
    }

    stage('coverage') {
      steps {
        jacoco(classPattern: '**/classes', deltaBranchCoverage: '0.01', deltaClassCoverage: '0.01', deltaComplexityCoverage: '0.01', deltaInstructionCoverage: '0.01', deltaLineCoverage: '0.01', deltaMethodCoverage: '0.01', execPattern: '**/**.exec', maximumBranchCoverage: '75', maximumClassCoverage: '75', maximumComplexityCoverage: '75', maximumInstructionCoverage: '75', maximumLineCoverage: '75', maximumMethodCoverage: '75', minimumBranchCoverage: '60', minimumClassCoverage: '60', minimumComplexityCoverage: '60', minimumInstructionCoverage: '60', minimumLineCoverage: '60', minimumMethodCoverage: '60', sourcePattern: '**/src/main/java', sourceInclusionPattern: '**/*.java,**/*.groovy,**/*.kt,**/*.kts', changeBuildStatus: true)
      }
    }

  }
}