package eclipse.errors.log.sending.core.entry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class Screenshot extends Entry
{
	public Screenshot(String a_entryName)
	{
		super(a_entryName);
	}
	
	@Override
	public InputStream getInputStream() throws IOException, InterruptedException 
	{
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		GC gc = new GC(shell);
	    final Image image = new Image(shell.getDisplay(), shell.getBounds());
	    gc.copyArea(image, 0, 0);
	    gc.dispose();
	    
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ImageLoader imageLoader = new ImageLoader();
        imageLoader.data = new ImageData[] { image.getImageData() };
        imageLoader.save(out, SWT.IMAGE_PNG);
        
	    return new ByteArrayInputStream(out.toByteArray());
	}
}
