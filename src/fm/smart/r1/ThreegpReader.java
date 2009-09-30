package fm.smart.r1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import com.coremedia.iso.FileRandomAccessDataSource;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoOutputStream;
import com.coremedia.iso.mdta.Sample;

public class ThreegpReader {

	private final static int ANDROID_AUDIO_TRACK_NUM = 3;

	private final File inputFile;

	public ThreegpReader(File file) throws FileNotFoundException {
		this.inputFile = file;
	}

	public void extractAmr(OutputStream outputStream) throws IOException {

		IsoFile iso = new IsoFile(new FileRandomAccessDataSource(inputFile));
		iso.parse();
		iso.parseMdats();

		IsoOutputStream isoOutput = new IsoOutputStream(outputStream, false);

		// write an AMR header
		isoOutput.write(new byte[] { 0x23, 0x21, 0x41, 0x4d, 0x52, 0x0a });

		// write the audio content
		for (Sample sample : iso.getTrack(ANDROID_AUDIO_TRACK_NUM)) {
			sample.getContent(isoOutput);
		}
		isoOutput.flush();
		isoOutput.close();
	}

}
