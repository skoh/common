// 테스트 서버만 동작하게 하는 방법
// 1. 테스트 브랜치 생성
// 2. GitLab 에서 테스트 브랜치로 변경
// 3. Jenkins 에서 관리자 메일 삭제
// 4. 위에서 Jenkinsfile.groovy 파일을 테스트 브랜치로 변경
node {
    println "params.BRANCH_NAME: ${params.BRANCH_NAME}"
    println "params.STAGE_NAMES: ${params.STAGE_NAMES}"
    println "params.STEP_NAMES: ${params.STEP_NAMES}"
    println "params.EMAIL: ${params.EMAIL}"
    println "params.MANAGER_EMAIL: ${params.MANAGER_EMAIL}"

    String DEV_SERVER_IP = '10.10.40.91' // 개발 서버
    String TEST_SERVER_IP = '10.10.40.150' // 테스트 서버
    println "JENKINS_URL: ${env.JENKINS_URL}"
    if (env.JENKINS_URL.contains(DEV_SERVER_IP)) {
        SERVER_IP = DEV_SERVER_IP
        SONAR_QUBE_PORT = '19000'
    } else if (env.JENKINS_URL.contains(TEST_SERVER_IP)) {
        SERVER_IP = TEST_SERVER_IP
        SONAR_QUBE_PORT = '9000'
    } else {
        error "Unknown JENKINS_URL: ${env.JENKINS_URL}"
    }
    println "SERVER_IP: ${SERVER_IP}"
    println "SONAR_QUBE_PORT: ${SONAR_QUBE_PORT}"

    currentBuild.result = 'SUCCESS'
    analyze = ''
    app = ''
    test = ''

    String gitlabRepoName
    String gitlabBranch
    String gitlabRepoHttpUrl
    String to = ''
    String cc = ''
    try {
        stage('scm') {
            println "gitlabActionType: ${env.gitlabActionType}"
            switch (env.gitlabActionType) {
                case 'MERGE':
                case 'NOTE':
                    STAGE_NAMES = 'oh-common'
                    STEP_NAMES = 'test'
                    gitlabRepoName = env.gitlabSourceRepoName
                    gitlabBranch = env.gitlabSourceBranch
                    gitlabRepoHttpUrl = env.gitlabSourceRepoHttpUrl
                    break
                case null:
                    STAGE_NAMES = params.STAGE_NAMES
                    STEP_NAMES = params.STEP_NAMES
                    gitlabRepoName = 'common'
                    gitlabBranch = params.BRANCH_NAME
                    gitlabRepoHttpUrl = 'https://github.com/skoh/common.git'
                    break
            }
            println "STAGE_NAMES: ${STAGE_NAMES}"
            println "STEP_NAMES: ${STEP_NAMES}"
            println "gitlabRepoName: ${gitlabRepoName}"
            println "gitlabBranch: ${gitlabBranch}"
            println "gitlabRepoHttpUrl: ${gitlabRepoHttpUrl}"

            if (!gitlabBranch?.trim()) {
                error 'Branch name is required.'
            }
            if (!STAGE_NAMES?.trim()) {
                error 'Stage names is required.'
            }
//            if (!STEP_NAMES?.trim()) {
//                error 'Step names is required.'
//            }

            git branch: gitlabBranch, credentialsId: 'gitlab-login', url: gitlabRepoHttpUrl

            println "gitlabUserEmail: ${env.gitlabUserEmail}"
            switch (env.gitlabActionType) {
                case 'MERGE':
                case 'NOTE':
                case 'PUSH':
                    to = emailextrecipients([culprits()])
                    if (!!env.gitlabUserEmail?.trim() && !to.contains(env.gitlabUserEmail)) {
                        to += !to?.trim() ? env.gitlabUserEmail : ',' + env.gitlabUserEmail
                    }
                    break
                case null:
                    String userId = currentBuild.rawBuild.getCause(hudson.model.Cause.UserIdCause.class).getUserId()
                    env.gitlabUserName = userId
                    String userEmail = User.get(userId).getProperty(hudson.tasks.Mailer.UserProperty.class).getAddress()
                    println "userId: ${userId} userEmail: ${userEmail}"
                    to = "${userEmail},${params.EMAIL}"
                    break
            }
        }

        String[] moduleNames = STAGE_NAMES.split(',')
                .collect { it.trim() }
        for (String moduleName : moduleNames) {
            buildAll(moduleName)
        }
//        error 'error'
    } catch (e) {
        currentBuild.result = 'FAILURE'
        cc = params.MANAGER_EMAIL
//        e.printStackTrace()
//        throw e
    } finally {
        String title = "The CI/CD result is ${currentBuild.result}. (${env.JOB_NAME}/${env.BUILD_NUMBER})"
        gitlabRepoHttpUrl = gitlabRepoHttpUrl.replace('.git', '')
        String body = """</br>
- Action Type : ${env.gitlabActionType == null ? 'MANUAL' : env.gitlabActionType}</br>
- Action User : ${env.gitlabUserName}${env.gitlabUserUsername == null ? '' : '(' + env.gitlabUserUsername + ')'}</br>
- Build Result : <a href='${env.JENKINS_URL}job/${env.JOB_NAME}/${env.BUILD_NUMBER}/flowGraphTable' target='build'>${env.JOB_NAME}/${env.BUILD_NUMBER}</a></br>
- Target Branch : <a href='${gitlabRepoHttpUrl}/-/tree/${gitlabBranch}' target='branch'>${gitlabRepoName}/${gitlabBranch}</a>"""
        if (env.gitlabMergeRequestLastCommit != null) {
            body += """</br>
- Last Commit : <a href='${gitlabRepoHttpUrl}/-/commit/${env.gitlabMergeRequestLastCommit}' target='commit'>${env.gitlabMergeRequestLastCommit}</a>"""
        }
        if (isBuild()) {
            body += """</br>
- App Download : ${app}"""
        }

        switch (env.gitlabActionType) {
            case 'MERGE':
            case 'NOTE':
                body += """</br>
- Merge Request : <a href='${gitlabRepoHttpUrl}/-/merge_requests/${gitlabMergeRequestIid}' target='mr'>${gitlabRepoName}/${gitlabMergeRequestIid}</a>"""
            case null:
                if (isAnalyze()) {
                    body += """</br>
- Analyze Result : ${analyze}"""
                }
                if (STEP_NAMES.contains('test')) {
                    body += """</br>
- Test Result : ${test}"""
                }
                break
        }

        // markdown
        String colorStart = "["
        String colorEnd = ""
        if (currentBuild.result == 'SUCCESS') {
            colorStart += "+"
            colorEnd += "+"
        } else {
            colorStart += "-"
            colorEnd += "-"
        }
        colorEnd += "]"

        String comment = "<b>${colorStart}${title}${colorEnd}</b>${body}"
        println "comment: ${comment}"
        addGitLabMRComment comment: comment

        // html
        colorStart = "<span style='background-color: #"
        if (currentBuild.result == 'SUCCESS') {
            colorStart += "C7F0D2"
        } else {
            colorStart += "FAC5CD"
        }
        colorStart += "'>"
        colorEnd = "</span>"

        println "to: ${to} cc: ${cc}"
        if (!!to?.trim() || !!cc?.trim()) {
            mail(mimeType: 'text/html',
                    subject: title,
                    body: comment,
                    to: to, cc: cc)
        }

        println """buildUser: ${emailextrecipients([buildUser()])} contributor: ${emailextrecipients([contributor()])}
culprits: ${emailextrecipients([culprits()])} developers: ${emailextrecipients([developers()])}
brokenTestsSuspects: ${emailextrecipients([brokenTestsSuspects()])} brokenBuildSuspects: ${emailextrecipients([brokenBuildSuspects()])}
previous: ${emailextrecipients([previous()])} requestor: ${emailextrecipients([requestor()])}
upstreamDevelopers: ${emailextrecipients([upstreamDevelopers()])}
"""
//        println """gitlabBranch: ${env.gitlabBranch} gitlabSourceBranch: ${env.gitlabSourceBranch}
//gitlabUserName: ${env.gitlabUserName} gitlabUserUsername: ${env.gitlabUserUsername}
//gitlabSourceRepoHomepage: ${env.gitlabSourceRepoHomepage} gitlabSourceRepoName: ${env.gitlabSourceRepoName}
//gitlabSourceNamespace: ${env.gitlabSourceNamespace} gitlabSourceRepoURL: ${env.gitlabSourceRepoURL}
//gitlabSourceRepoSshUrl: ${env.gitlabSourceRepoSshUrl} gitlabSourceRepoHttpUrl: ${env.gitlabSourceRepoHttpUrl}
//gitlabMergeCommitSha: ${env.gitlabMergeCommitSha} gitlabMergeRequestTitle: ${env.gitlabMergeRequestTitle}
//gitlabMergeRequestDescription: ${env.gitlabMergeRequestDescription} gitlabMergeRequestId: ${env.gitlabMergeRequestId}
//gitlabMergeRequestIid: ${env.gitlabMergeRequestIid} gitlabMergeRequestState: ${env.gitlabMergeRequestState}
//gitlabMergedByUser: ${env.gitlabMergedByUser} gitlabMergeRequestAssignee: ${env.gitlabMergeRequestAssignee}
//gitlabMergeRequestLastCommit: ${env.gitlabMergeRequestLastCommit} gitlabMergeRequestTargetProjectId: ${env.gitlabMergeRequestTargetProjectId}
//gitlabTargetBranch: ${env.gitlabTargetBranch} gitlabTargetRepoName: ${env.gitlabTargetRepoName}
//gitlabTargetNamespace: ${env.gitlabTargetNamespace} gitlabTargetRepoSshUrl: ${env.gitlabTargetRepoSshUrl}
//gitlabTargetRepoHttpUrl: ${env.gitlabTargetRepoHttpUrl} gitlabBefore: ${env.gitlabBefore}
//gitlabAfter: ${env.gitlabAfter} gitlabTriggerPhrase: ${env.gitlabTriggerPhrase}
//"""
    }
}

// 빌드
void buildAll(String moduleName) {
    stage(moduleName) {
        if (STAGE_NAMES.contains(moduleName)) {
            try {
                build(moduleName)
                analyze(moduleName)
                publish(moduleName)
            } finally {
                analyze += "<a href='http://${SERVER_IP}:${SONAR_QUBE_PORT}/dashboard?id=${moduleName}' target='analyze'>${moduleName}</a> "

                String build = "<a href='${env.JENKINS_URL}job/${env.JOB_NAME}/${env.BUILD_NUMBER}/execution/node/3/ws/${moduleName}/build/"
                app += "${build}libs' target='app'>${moduleName}</a> "
                test += "${build}reports/tests/test/index.html' target='test'>${moduleName}</a> "
            }
        }
    }
}

// 빌드
void build(String moduleName) {
    if (isBuild()) {
        bat "call build ${moduleName}"
    }
}

// 정적 분석, 테스트 커버리지
void analyze(String moduleName) {
    if (isAnalyze()) {
//        withSonarQubeEnv() {
        String test = STEP_NAMES.contains('test') ? ' test' : ''
        bat """
set SONAR_URL=-Dsonar.host.url=http://${SERVER_IP}:${SONAR_QUBE_PORT}
call sonar ${moduleName}${test}
"""
    }
//    }
//    timeout(time: 2, unit: 'MINUTES') {
//        def qg = waitForQualityGate()
//        if (qg.status != 'OK') {
//            error "Pipeline aborted due to quality gate failure: ${qg.status}"
//        }
//    }
}

// 배포
void publish(String moduleName) {
    if (STEP_NAMES == 'publish') {
        bat "call publish ${moduleName}"
    }
}

// 빌드 여부
boolean isBuild() {
    return (STEP_NAMES.contains('build')
            || STEP_NAMES.contains('analyze')
            || STEP_NAMES.contains('test'))
}

// 분석 여부
boolean isAnalyze() {
    return (STEP_NAMES.contains('analyze')
            || STEP_NAMES.contains('test'))
}

