import java.io.*;
import org.apache.commons.io.FileUtils;

public class Test {
	public static void main(String args[]) throws Exception{
		try {
			String data = FileUtils.readFileToString(new File("test.cpl"));
			System.out.println(data);

		} catch (Exception e) {
			System.out.println("no existe");
		}
	}
}