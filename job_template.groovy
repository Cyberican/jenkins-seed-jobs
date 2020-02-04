// ########## CONFIGURE THIS ##########
def customerNumber = '_template_2.1'
def customerLabel = '_template_2.1'
def jobPrefix = 'customer'
// ####################################

// Do not change this nomenclature!
def customerGitUrl = 'ssh://git@bitbucket.com:2222/cus/' + customerNumber + '_build.git'
def credentialsIdJenkins = 'jenkins_key-XX_XX_XX_X'

def ROOT_FOLDER = 'USER'
folder(ROOT_FOLDER){ displayName('Customer Development') }
def FOLDER_NAME = ROOT_FOLDER + '/' + customerNumber
folder(FOLDER_NAME){ displayName(customerLabel) }

// ###### LIST THE JOBS YOU NEED ######
pipelineJob(FOLDER_NAME + '/' + jobPrefix + '_ci_check') {
    description('Check-Job')
    disabled() // remove that line for your customer!
    triggers { cron('H 1 * * *') }
    definition {
        cpsScm {
            scm {
                git	{
                    remote {
                        url(customerGitUrl)
                        credentials(credentialsIdJenkins)
                    }
                    branch('master')
                    scriptPath('Jenkinsfile')
                }
            }
        }
    }
    configure {
        it / definition / lightweight(true)
    }
}
pipelineJob(FOLDER_NAME + '/' + jobPrefix + '_ci_delivery') {
    description('Delivery-Job')
    disabled() // remove that line for your customer!
    definition {
        cpsScm {
            scm {
                git	{
                    remote {
                        url(customerGitUrl)
                        credentials(credentialsIdJenkins)
                    }
                    branch('master')
                    scriptPath('Jenkinsfile')
                }
            }
        }
    }
    parameters {
        stringParam('start_revision', '', 'Description Message Here.')
        stringParam('end_revision', '', 'Description Message Here.')
        textParam('script_editor','','Description Message Here.\n')
    }

    configure {
        it / definition / lightweight(true)
    }
}
pipelineJob(FOLDER_NAME + '/' + jobPrefix + '_ci_deployment') {
    description('Deploy-Job')
    disabled() // remove that line for your customer!
    definition {
        cpsScm {
            scm {
                git	{
                    remote {
                        url(customerGitUrl)
                        credentials(credentialsIdJenkins)
                    }
                    branch('master')
                    scriptPath('Jenkinsfile')
                }
            }
        }
    }
    configure {
        it / definition / lightweight(true)
    }
}

