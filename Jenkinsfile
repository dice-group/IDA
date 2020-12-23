pipeline {
  agent {
    docker {
      image 'maven:3-alpine'
      args '-v /root/.m2:/root/.m2'
    }

  }
  stages {
    stage('Verify') {
      agent {
        docker {
          args '-v /root/.m2:/root/.m2'
          image 'maven:3-alpine'
        }

      }
      steps {
        sh 'mvn clean package'
        cobertura(classCoverageTargets: '60', coberturaReportFile: 'target/site/cobertura/cobertura.xml', conditionalCoverageTargets: '60', fileCoverageTargets: '60', lineCoverageTargets: '60', methodCoverageTargets: '60', packageCoverageTargets: '60')
      }
    }

  }
}