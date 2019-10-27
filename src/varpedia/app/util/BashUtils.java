package varpedia.app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BashUtils {
	
	public static final CommandOutput runCommand(ProcessBuilder _pb, String command, boolean wantOutput) {
		
		ProcessBuilder pb = _pb;
		if (pb == null) {
			pb = new ProcessBuilder();
		}
		pb.command("bash", "-c", command);
		
		try {
			
			Process p = pb.start();
			String stdOutput = "", stdError = "";
			
			if (wantOutput) {
				
				BufferedReader stdOutputReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
					   
				String line;
				while ((line = stdOutputReader.readLine()) != null) {
					stdOutput += line + "\n";
				}
				stdOutput.trim();
				
			}

			int exitCode = p.waitFor();
			if (exitCode != 0) {
				
				BufferedReader stdErrReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String line;
				while ((line = stdErrReader.readLine()) != null) {
					stdError += line;
				}
				System.out.println("Exit code: " + exitCode + ", Command: " + command + ", Error output: " + stdError);
				
			}
			return new CommandOutput(stdOutput, stdError, exitCode);
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	public static class CommandOutput {
		
		private final String _stdOut,
							 _stdErr;
		private final int _exitCode;
		
		public CommandOutput(String stdOut, String stdErr, int exitCode) {
			
			_stdOut = stdOut;
			_stdErr = stdErr;
			_exitCode = exitCode;
			
		}
		
		public String getStdOut() {
			return _stdOut;
		}

		public String getStdErr() {
			return _stdErr;
		}
		
		public int getExitCode() {
			return _exitCode;
		}
		
	}

}
