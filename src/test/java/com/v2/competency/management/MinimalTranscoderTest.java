package com.v2.competency.management;
import java.io.IOException;

import com.google.cloud.video.transcoder.v1.CreateJobRequest;
import com.google.cloud.video.transcoder.v1.Job;
import com.google.cloud.video.transcoder.v1.JobName;
import com.google.cloud.video.transcoder.v1.LocationName;
import com.google.cloud.video.transcoder.v1.TranscoderServiceClient;

public class MinimalTranscoderTest {
  public static void main(String[] args) {
    // 1. Double-check this is your exact Project ID.
    String projectId = "contactaiassessments";
    String location = "asia-south1";

    // 2. Use a direct, simple path to one of your files.
    String inputUri = "gs://zimulate/Compose Test/v1.mp4";
    String outputUri = "gs://zimulate/Compose Test/";

    System.out.println("Submitting minimal job with the following details:");
    System.out.println("Project: " + projectId);
    System.out.println("Location: " + location);
    System.out.println("Input: " + inputUri);
    System.out.println("Output: " + outputUri);

    try (TranscoderServiceClient client = TranscoderServiceClient.create()) {
      // 3. Use a standard, built-in preset. This is the simplest possible config.
      Job job = Job.newBuilder()
          .setInputUri(inputUri)
          .setOutputUri(outputUri)
          .setTemplateId("preset/web-hd")
          .build();

      CreateJobRequest request = CreateJobRequest.newBuilder()
          .setParent(LocationName.of(projectId, location).toString())
          .setJob(job)
          .build();

      // 4. This is the call that is failing.
      Job response = client.createJob(request);
      System.out.println("--- SUCCESS ---");
      System.out.println("Successfully submitted single-file job: " + response.getName());

    } catch (Exception e) {
      System.err.println("--- ERROR ---");
      System.err.println("Job submission failed: " + e.getMessage());
      e.printStackTrace(); // Print the full error trace
    }
  }
  
  public static void getJobStatus(String projectId, String location, String jobId) throws IOException {

	    try (TranscoderServiceClient transcoderServiceClient = TranscoderServiceClient.create()) {

	      // 1. Construct the full job name from the parts.
	      String jobName = JobName.of(projectId, location, jobId).toString();

	      // 2. Call the API to get the job details.
	      Job job = transcoderServiceClient.getJob(jobName);

	      // 3. Print the job's current state.
	      System.out.println("Job status for '" + jobId + "': " + job.getState());

	      // 4. If the job failed, print the error details.
	      if (job.hasError()) {
	        System.err.println("Job failed with error: " + job.getError().getMessage());
	        System.err.println("Error details: " + job.getError().getDetailsList());
	      }
	    } catch (Exception e) {
	      System.err.println("Error fetching job status: " + e.getMessage());
	      e.printStackTrace();
	    }
	  }
}