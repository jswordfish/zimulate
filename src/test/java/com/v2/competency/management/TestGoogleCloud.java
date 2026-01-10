package com.v2.competency.management;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.PartMaker;
import com.google.cloud.video.transcoder.v1.CreateJobRequest;
import com.google.cloud.video.transcoder.v1.EditAtom;
import com.google.cloud.video.transcoder.v1.ElementaryStream;
import com.google.cloud.video.transcoder.v1.Job;
import com.google.cloud.video.transcoder.v1.JobConfig;
import com.google.cloud.video.transcoder.v1.JobName;
import com.google.cloud.video.transcoder.v1.LocationName;
import com.google.cloud.video.transcoder.v1.MuxStream;
import com.google.cloud.video.transcoder.v1.TranscoderServiceClient;
import com.google.cloud.video.transcoder.v1.VideoStream;
public class TestGoogleCloud {
	
	
	String projectId = "contactaiassessments";
	Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
	
	@Test
	public void testListObjectsInBucket() {
		String prefix = "Compose Test/";
		Page<Blob> blobs = storage.list(
				"zimulate",
		          Storage.BlobListOption.prefix(prefix)
		      );
	    for (Blob blob : blobs.iterateAll()) {
	      System.out.println(blob.getName());
	    }
	}
	
	@Test
	public void testFetchBuckets() throws IOException {
		
		authenticateImplicitWithAdc(projectId);
	}
	
	public  void authenticateImplicitWithAdc(String project) throws IOException {

	    // *NOTE*: Replace the client created below with the client required for your application.
	    // Note that the credentials are not specified when constructing the client.
	    // Hence, the client library will look for credentials using ADC.
	    //
	    // Initialize client that will be used to send requests. This client only needs to be created
	    // once, and can be reused for multiple requests.
	    

	    System.out.println("Buckets:");
	    Page<Bucket> buckets = storage.list();
	    for (Bucket bucket : buckets.iterateAll()) {
	      System.out.println(bucket.toString());
	    }
	    System.out.println("Listed all storage buckets.");
	    String initFolder = "role_plays";
	    String rolePlay = "Test Roleplay 1";
	    String email = "test@test.com";
	    Integer att = 1;
	    String companyId = "INF";
	    createFolders(initFolder, rolePlay, email, att, companyId);
	    
	  }

		private void createFolders(Object ...objects ) {
			String folderPath = "";
			for(int i=0;i<objects.length;i++) {
				folderPath += objects[i].toString()+"/";
				createIfNeededFolder(folderPath);
			}
			
		}
		
		private void createIfNeededFolder(String folder) {
			BlobId blobId = BlobId.of("zimulate", folder);
		    
		    Blob blob = storage.get(blobId);
		    if(blob != null && blob.exists()) {
		    	return;
		    }
		    else {
		    	BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
	            storage.create(blobInfo, new byte[0]);
		    }
		}
		
		@Test
		public void testMergeVideos() throws Exception {
			List<String> list = Arrays.asList("Compose Test/v1.mp4", "Compose Test/v2.mp4", "Compose Test/v3.mp4", "Compose Test/v4.mp4");
			mergeVideos("contactaiassessments", "asia-south1", "zimulate", list, projectId);
			
		}
		
		public static void mergeVideos(
			      String projectId,
			      String location,
			      String bucketName,
			      List<String> sourceFiles,
			      String destinationFile) throws IOException {

			    String outputUri = "gs://" + bucketName + "/" + destinationFile;

			    try (TranscoderServiceClient transcoderServiceClient = TranscoderServiceClient.create()) {

			      List<EditAtom> editList = sourceFiles.stream().map(file -> {
			        String inputUri = "gs://" + bucketName + "/" + file;
			        return EditAtom.newBuilder()
			            .setKey(file)
			            .addInputs(inputUri)
			            .build();
			      }).collect(Collectors.toList());

			      // --- CORRECTION IS HERE ---
			      JobConfig jobConfig =
			          JobConfig.newBuilder()
			              .addAllEditList(editList)
			              .addElementaryStreams( // We only define the video stream now
			                  ElementaryStream.newBuilder()
			                      .setKey("video-stream0")
			                      .setVideoStream(
			                          VideoStream.newBuilder().setH264(
			                              VideoStream.H264CodecSettings.newBuilder().setBitrateBps(550000))))
			              // The entire audio stream configuration has been removed.
			              .addMuxStreams(
			                  MuxStream.newBuilder()
			                      .setKey("mp4")
			                      .setContainer("mp4")
			                      // The MuxStream now only includes the video stream key.
			                      .addAllElementaryStreams(Arrays.asList("video-stream0")))
			              .build();

			      CreateJobRequest createJobRequest =
			          CreateJobRequest.newBuilder()
			              .setParent(LocationName.of(projectId, location).toString())
			              .setJob(
			                  Job.newBuilder()
			                      .setOutputUri(outputUri)
			                      .setConfig(jobConfig)
			                      .build())
			              .build();

			      Job job = transcoderServiceClient.createJob(createJobRequest);
			      System.out.println("Successfully submitted transcoding job: " + job.getName());
			      System.out.println("Output will be at: " + outputUri);

			    } catch (Exception e) {
			      System.err.println("An error occurred: " + e.toString());
			      throw new IOException(e);
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
		
		@Test			  
		public void testgetJobStatus() throws IOException {
			getJobStatus(projectId, "asia-south1", "5786d5a3-b5d1-4947-8f65-205ab1faa6f6");
		}
		
		
}
