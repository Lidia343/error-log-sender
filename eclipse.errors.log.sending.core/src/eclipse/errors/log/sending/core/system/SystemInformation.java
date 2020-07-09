package eclipse.errors.log.sending.core.system;

import java.io.IOException;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;

import eclipse.errors.log.sending.core.exceptions.BlockedThreadException;
import eclipse.errors.log.sending.core.util.AppUtil;

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
			String result = AppUtil.getInputStreamAsString(processBuilder);
			
			String ramAmountInfo = "";
			for (char c : result.toCharArray())
			{
				if (AppUtil.isDigit(c)) ramAmountInfo += Character.toString(c);
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
			String result = AppUtil.getInputStreamAsString(processBuilder);
			
			String ramAmountLine = "";
			int beginIndex = "MemTotal:".length();
			for (char c : result.substring(beginIndex).toCharArray())
			{
				if (AppUtil.isDigit(c)) ramAmountLine += Character.toString(c);
				else if (!ramAmountLine.equals("")) break;
			}
			long ramAmount = Math.round((double) (Long.parseLong(ramAmountLine) / Math.pow(1024, 2)));
			return Long.toString(ramAmount) + " ГБ";
		}
		return null;
	}
	
	public String getScreenResolution ()
	{
		Device device = Display.getDefault();
		char c = (char) 215; //Код символа для разделения параметров разрешения экрана
		return  Integer.toString(device.getBounds().width) + c + device.getBounds().height;
	}
}
