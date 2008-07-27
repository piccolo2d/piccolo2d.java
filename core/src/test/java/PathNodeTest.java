import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import junit.framework.TestCase;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PObjectOutputStream;

public class PathNodeTest extends TestCase {

	public PathNodeTest(String name) {
		super(name);
	}
	
	public void testCopy() {
		PPath p = PPath.createEllipse(0, 0, 100, 100);
		PBounds b = p.getBounds();
		p = (PPath) p.clone();
		assertEquals(p.getBounds(), b);
	}	
	
	public void testSaveToFile() {
		PPath p = PPath.createEllipse(0, 0, 100, 100);
		PBounds b = p.getBounds();
		try {
			File file = new File("myfile");
			FileOutputStream fout = new FileOutputStream(file);
			PObjectOutputStream out = new PObjectOutputStream(fout);
			out.writeObjectTree(p);
			out.flush();
			out.close();
			
			FileInputStream fin = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(fin);
			p = (PPath) in.readObject();
			assertEquals(p.getBounds(), b);
			file.delete();
		} catch (FileNotFoundException e) {
			assertTrue(false);
		} catch (ClassNotFoundException e) {
			assertTrue(false);
		} catch (IOException e) {
			assertTrue(false);
		}
	}
}
