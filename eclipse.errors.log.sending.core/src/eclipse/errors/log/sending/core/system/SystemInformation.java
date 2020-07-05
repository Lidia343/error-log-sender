package eclipse.errors.log.sending.core.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;

import eclipse.errors.log.sending.core.exceptions.BlockedThreadException;

public class SystemInformation 
{
	private String m_osName;
	
	public SystemInformation()
	{
		m_osName = System.getProperty("os.name");
	}
	
	public String getOsName ()
	{
		return m_osName;
	}
	
	public String getUsername ()
	{
		return System.getProperty("user.name");
	}
	
	public String getRamAmount () throws IOException, InterruptedException, BlockedThreadException
	{
		String osName = m_osName.toLowerCase();
		
		ProcessBuilder processBuilder = new ProcessBuilder();
		if (osName.contains("win"))
		{
			processBuilder.command("cmd.exe", "/c", "wmic memorychip get Capacity");
			String result = getInputStream(processBuilder);
			
			String ramAmountInfo = "";
			for (char c : result.toCharArray())
			{
				if (isDigit(c)) ramAmountInfo += Character.toString(c);
				else if (!ramAmountInfo.equals(""))
				{
					if (!ramAmountInfo.endsWith(" ")) ramAmountInfo += " ";
				}
			}
			
			String buffer = "";
			long ramAmount = 0;
			for (char c : ramAmountInfo.toCharArray())
			{
				if (c != ' ') buffer += Character.toString(c);
				else
				{
					ramAmount += (Long.parseLong(buffer));
					buffer = "";
				}
			}
			
			return Double.toString((double) (ramAmount / Math.pow(1024, 3))) + " ГБ";
		}
		if (osName.contains("nux"))
		{
			processBuilder.command("bash", "-c", "cat /proc/meminfo");
			String result = getInputStream(processBuilder);
			
			String ramAmountLine = "";
			int beginIndex = "MemTotal:".length();
			for (char c : result.substring(beginIndex).toCharArray())
			{
				if (isDigit(c)) ramAmountLine += Character.toString(c);
				else if (!ramAmountLine.equals("")) break;
			}
			long ramAmount = Math.round((double) (Long.parseLong(ramAmountLine) / Math.pow(1024, 2)));
			return Long.toString(ramAmount) + " ГБ";
		}
		return null;
	}
	
	private String getInputStream (ProcessBuilder a_processBuilder) throws IOException, InterruptedException, BlockedThreadException
	{
		Process process = a_processBuilder.start();
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufReader = new BufferedReader (new InputStreamReader (process.getInputStream()));
		
		String line;
		while ((line = bufReader.readLine()) != null)
		{
			stringBuilder.append(line + "\n");
		}
		if (process.waitFor() != 0) throw new BlockedThreadException();
		return stringBuilder.toString();
	}

	private boolean isDigit (char c) 
	{
		String s = Character.toString(c);
		try 
		{
			Integer.parseInt(s);
		} 
		catch (NumberFormatException e) 
		{
			return false;
		}
		return true;
	}
	
	
	public String getScreenResolution ()
	{
		Device device = Display.getDefault();
		char c = (char) 215; //Код символа для разделения параметров разрешения экрана
		return  Integer.toString(device.getBounds().width) + c + device.getBounds().height;
	}
}
