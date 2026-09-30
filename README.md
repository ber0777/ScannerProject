# ThesisScan

ThesisScan is a terminal based Java application that extracts editable text from images using Tesseract OCR. The OCR engine sits behind `OCRService`, so another engine or a future GUI can reuse the image processing, statistics, file and result model layers.

## Requirements

- Java 17 or newer (JDK, including `javac`)
- Tesseract OCR for actual recognition
- Tesseract language data for the languages you use: `rus`, `eng`, `hye`
- Windows, macOS, or Linux; no Maven or Gradle is required

Install Tesseract using the package or installer for your operating system, then install the Russian, English and Armenian trained data files (`rus.traineddata`, `eng.traineddata`, `hye.traineddata`) in its `tessdata` directory. Tesseract's command line must be available on `PATH`, or set `tesseract.path` in `config.properties`. Set `tessdata.path` if the data directory is not found automatically.

## Configure and compile

Edit `config.properties` if needed:

```properties
tesseract.path=C:/path/to/tesseract.exe
tessdata.path=C:/path/to/tessdata
```

Paths can also use forward slashes on Windows. From the project root, compile all application sources:

```text
javac -encoding UTF-8 -d out src/*.java src/model/*.java src/service/*.java src/util/*.java src/ui/*.java
```

Run the interactive terminal menu:

```text
java -cp out Main
```

Start the Swing desktop interface with:

```text
java -cp out GuiMain
```

The desktop interface shares the same OCR services as the CLI. Add an image using the file chooser, drag and drop, or paste an image from the system clipboard with Ctrl+V / the Paste button. Select an OCR language and optional preprocessing; drag across the preview to select a source-pixel region. OCR runs in a background worker. Edit the recognized text, search with previous/next, and save as UTF-8 TXT with the Save button or Ctrl+S.

Direct command mode accepts a source image and OCR language:

```text
java -cp out Main test-data/russian.jpg rus
java -cp out Main test-data/english.jpg eng
java -cp out Main test-data/armenian.jpg hye
java -cp out Main test-data/mixed.jpg rus+eng
```

The interactive menu supports full image recognition, coordinate-based cropping, optional grayscale/contrast/binarization/upscaling, text statistics, showing and saving the latest result, and a plain text history in `data/history.txt`. Supported image formats are JPG, JPEG, PNG and BMP. Saved text uses UTF-8.

## Test images

Put your own test images in `test-data/`. Suggested names are `russian.jpg`, `english.jpg`, `armenian.jpg`, and `mixed.jpg`. No image fixtures are included. The integration test looks for `test-data/ocr-sample.png` and uses English OCR.

## Run tests

Compile test classes after compiling the application:

```text
javac -encoding UTF-8 -cp out -d out tests/*.java
java -cp out TextStatisticsTest
java -cp out ImagePreprocessorTest
java -cp out FileUtilsTest
java -cp out OCRServiceTest
```

The OCR test reports a skip if no sample image or Tesseract configuration is available. Tests use only Java's standard library and small temporary image/text fixtures.

## Languages and common errors

Use `rus` for Russian, `eng` for English, `hye` for Armenian, and combinations such as `rus+eng` when both language packs are installed. A missing executable produces a setup message; an OCR language error usually means its trained data is missing or `tessdata.path` is incorrect. Invalid, unreadable, corrupted, or unsupported images are rejected before recognition. Crop coordinates must fit inside the original image.

## Future GUI

A future Swing interface can call `OCRService`, `ImagePreprocessor`, `TextStatisticsService`, and `FileUtils` directly and display `OCRResult`. The current project intentionally has no GUI classes or web interface.
