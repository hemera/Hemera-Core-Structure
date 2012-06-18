package hemera.core.structure.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class ThroughputTest {
	
	private final Scanner scanner;
	
	public ThroughputTest() {
		this.scanner = new Scanner(System.in);
	}
	
	public void start() throws Exception {
		final int count = this.askForInteger("Please enter the number of concurrent requests:");
		final String url = this.askForString("Please enter the request URL:");
		final long start = System.currentTimeMillis();
		this.sendRequests(count, url);
		final long end = System.currentTimeMillis();
		final long elapsed = end-start;
		System.out.println(count + " concurrent requests cost: " + elapsed + "ms");
	}
	
	private String askForString(final String prompt) {
		while (true) {
			System.out.println(prompt);
			final String input = this.scanner.nextLine().trim();
			if (input.length() > 0) return input;
		}
	}

	private int askForInteger(final String prompt) {
		while (true) {
			System.out.println(prompt);
			final String input = this.scanner.nextLine();
			try {
				final int value = Integer.valueOf(input);
				return value;
			} catch (final Exception e) {
				System.out.println("Invalid input. Please enter an integer.");
			}
		}
	}
	
	private void sendRequests(final int count, final String url) throws InterruptedException {
		final CountDownLatch startingLatch = new CountDownLatch(1);
		final CountDownLatch completionLatch = new CountDownLatch(count);
		System.out.println("Creating request senders...");
		for (int i = 0; i < count; i++) {
			final Thread thread = new Thread(new Request(startingLatch, completionLatch, url));
			thread.start();
		}
		startingLatch.countDown();
		System.out.println("Sending started...");
		completionLatch.await();
	}
	
	public static void main(final String[] args) throws Exception {
		new ThroughputTest().start();
	}
	
	private class Request implements Runnable {
		
		private final CountDownLatch startingLatch;
		private final CountDownLatch completionLatch;
		private final String url;
		
		private Request(final CountDownLatch startingLatch, final CountDownLatch completionLatch, final String url) {
			this.startingLatch = startingLatch;
			this.completionLatch = completionLatch;
			if (!url.startsWith("http://") && !url.startsWith("https://")) {
				this.url = "http://" + url;
			} else {
				this.url = url;
			}
		}
		
		@Override
		public void run() {
			try {
				this.startingLatch.await();
				this.send();
				this.completionLatch.countDown();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		
		private void send() throws Exception {
			final URL url = new URL(this.url);
			final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			final InputStreamReader streamreader = new InputStreamReader(connection.getInputStream());
			final BufferedReader reader = new BufferedReader(streamreader);
		    while (reader.readLine() != null);
		    reader.close();
		}
	}
}
