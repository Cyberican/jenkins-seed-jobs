import groovy.json.JsonSlurper;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

def jsonSlurper = new JsonSlurper()

public class JobTemplate {
	// ########## CONFIGURE THIS ##########
	String customerNumber, customerLabel, jobPrefix, customerGitUrl, credentialsIdJenkins, usingTemplate
	public JobTemplate(def customerNumber, def customerLabel, def jobPrefix, def customerGitUrl, def credentialsIdJenkins, def usingTemplate){
		this.customerNumber = customerNumber
		this.customerLabel = customerLabel
		this.jobPrefix = jobPrefix
		this.customerGitUrl = customerGitUrl
		this.credentialsIdJenkins = credentialsIdJenkins
		this.usingTemplate = usingTemplate
	}

	public String getCustomerNumber(){
		return this.customerNumber
	}

	public String getJobLabel(){
		return this.customerLabel
	}

	public String getPrefix(){
		return this.jobPrefix
	}
	public String getTemplateName(){
		return this.usingTemplate
	}
}

task createDirectories(){
		description 'Creates the main directories for jobs.'
    doFirst {
      println "Removing Older Jobs"
      ["rm","-rfv","./jobs/customizing/*"].execute()
    }
    doLast {
      if (!file("jobs/customizing").exists()){
        println "Creating Jobs Directory"
        println "Initialize Directories"
        file("jobs/customizing").mkdirs()
      }
    }
}

task generateJobs(dependsOn: createDirectories) {
	description 'Generates the seed job configuration.'
	def seed_jobs = file("seed_jobs_dataset.json").text
	Object object = jsonSlurper.parseText(seed_jobs)
	doLast {
		try {
			for (Object s in object.jobs.pipelineJob) {
				def jobTemplate = new JobTemplate(s.customerNumber, s.customerLabel, s.jobPrefix, "${object.customerGitUrl}".toString(), "${object.credentialsIdJenkins}".toString(), s.usingTemplate)
				def srcTemplate = file(jobTemplate.getTemplateName() + ".groovy").text
				def f = file("jobs/customizing/jobs_" + jobTemplate.getCustomerNumber() + ".groovy")
				f.write(
					srcTemplate.replace("%{customer_number}",jobTemplate.getCustomerNumber())
					.replace("%{customer_label}",jobTemplate.getJobLabel() + " [${jobTemplate.getCustomerNumber()}]")
					.replace("%{job_prefix}",jobTemplate.getPrefix())
					.replace("%{customer_git_url}",object.customerGitUrl),
					'UTF8')
				}
			def date = new Date()
		  def zipfileName = "jobs/jobs_configuration-" + date.format('yyyyMMddHHmmss') + ".7z"
			["7z","a",zipfileName,"jobs"].execute()
		} catch(Exception e) { }
	}
}

