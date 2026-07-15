package com.quick.file; // Declares the package where this class belongs

import com.quick.model.FileStats; // Imports the FileStats model used for file statistics
import com.quick.path.QuickPath; // Imports QuickPath for path-related helper methods

import java.nio.file.Files; // Imports utility methods for file operations
import java.nio.file.Path; // Imports the Path interface for file system paths
import java.nio.file.Paths; // Imports utility methods for creating Path objects
import java.nio.file.StandardCopyOption; // Imports copy and move options
import java.nio.file.StandardOpenOption; // Imports file write and append options
import java.nio.file.attribute.BasicFileAttributes; // Imports file attribute metadata
import java.time.Instant; // Imports Instant for timestamps
import java.util.Arrays; // Imports array utilities
import java.util.List; // Imports List collection
import java.util.function.Consumer; // Imports Consumer for callbacks
import java.util.regex.Matcher; // Imports Matcher for regex matching
import java.util.regex.Pattern; // Imports Pattern for regex operations
import java.util.stream.Collectors; // Imports Collectors for stream collection

public class QuickFile { // Declares the QuickFile class

    private Path path; // Stores the current file path
    private String cachedContent; // Stores cached file content to reduce repeated reads
    private Instant cachedLastModified; // Stores the last modified time of the cached content

    public QuickFile(String path) { // Constructor that creates a QuickFile from a string path
        this.path = Paths.get(path); // Converts the string path into a Path object
    } // End of constructor

    public QuickPath path() { // Returns the current file path wrapped as QuickPath
        return new QuickPath(path); // Creates and returns a QuickPath from the internal path
    } // End of path

    public Path toPath() { // Returns the raw Path object
        return path; // Returns the internal path directly
    } // End of toPath

    public QuickPath absolute() { // Returns the absolute path as QuickPath
        return new QuickPath(path.toAbsolutePath()); // Converts the path to absolute form and wraps it
    } // End of absolute

    public String absolutePath() { // Returns the absolute path as a string
        return path.toAbsolutePath().toString(); // Converts the absolute path to string
    } // End of absolutePath

    public QuickFile create() { // Creates a new file and throws an error if it already exists
        try { // Starts protected block for file creation
            Path parent = path.getParent(); // Gets the parent directory of the file
            if (parent != null) { // Checks if the file has a parent directory
                Files.createDirectories(parent); // Creates parent directories if they do not exist
            } // End of parent null check

            if (Files.exists(path)) { // Checks whether the file already exists
                throw new QuickFileException("QuickFile create error: file already exists: " + path); // Throws a custom exception if the file exists
            } // End of exists check

            Files.createFile(path); // Creates the file
            cachedContent = ""; // Updates the cache with empty content for the new file
            cachedLastModified = lastModifiedOrNull(); // Updates the cached last modified time
            return this; // Returns the current QuickFile instance for chaining
        } catch (QuickFileException e) { // Re-throws custom exceptions without modification
            throw e; // Re-throws the same QuickFileException
        } catch (Exception e) { // Catches any other exception
            throw new QuickFileException("QuickFile create error: " + e.getMessage(), e); // Wraps it in a QuickFileException
        } // End of try-catch
    } // End of create

    public QuickFile createIfMissing() { // Creates the file only if it does not already exist
        try { // Starts protected block for conditional file creation
            Path parent = path.getParent(); // Gets the parent directory of the file
            if (parent != null) { // Checks if the file has a parent directory
                Files.createDirectories(parent); // Creates parent directories if needed
            } // End of parent null check

            if (Files.notExists(path)) { // Checks whether the file does not exist
                Files.createFile(path); // Creates the file if missing
            } // End of notExists check

            refreshCache(); // Refreshes the content cache after creation
            return this; // Returns the current instance for chaining
        } catch (Exception e) { // Catches any exception
            throw new QuickFileException("QuickFile create error: " + e.getMessage(), e); // Wraps it in a custom exception
        } // End of try-catch
    } // End of createIfMissing

    public QuickFile touch() { // Ensures the file exists and updates its last modified time
        try { // Starts protected block for touch operation
            createIfMissing(); // Creates the file if it does not already exist
            Files.setLastModifiedTime( // Updates the file's last modified timestamp
                    path, // Uses the current file path
                    java.nio.file.attribute.FileTime.fromMillis(System.currentTimeMillis()) // Sets the time to now
            ); // End of setLastModifiedTime call
            cachedLastModified = lastModifiedOrNull(); // Updates the cached last modified time
            return this; // Returns the current instance for chaining
        } catch (Exception e) { // Catches any exception
            throw new QuickFileException("QuickFile touch error: " + e.getMessage(), e); // Wraps it in a custom exception
        } // End of try-catch
    } // End of touch

    public QuickFile write(String text) { // Writes text to the file and replaces existing content
        try { // Starts protected block for write operation
            Path parent = path.getParent(); // Gets the parent directory of the file
            if (parent != null) { // Checks if the file has a parent directory
                Files.createDirectories(parent); // Creates parent directories if needed
            } // End of parent null check

            Files.writeString( // Writes text into the file
                    path, // Uses the current file path
                    text, // Writes the provided text
                    StandardOpenOption.CREATE, // Creates the file if missing
                    StandardOpenOption.TRUNCATE_EXISTING // Replaces existing file content
            ); // End of writeString call

            cachedContent = text; // Updates the cached content with the written text
            cachedLastModified = lastModifiedOrNull(); // Updates the cached last modified time
            return this; // Returns the current instance for chaining
        } catch (Exception e) { // Catches any exception
            throw new QuickFileException("QuickFile write error: " + e.getMessage(), e); // Wraps it in a custom exception
        } // End of try-catch
    } // End of write

    public QuickFile writeLines(List<String> lines) { // Writes a list of lines to the file
        return write(String.join(System.lineSeparator(), lines)); // Joins lines with the system line separator and writes them
    } // End of writeLines

    public QuickFile append(String text) { // Appends text to the end of the file
        try { // Starts protected block for append operation
            Path parent = path.getParent(); // Gets the parent directory of the file
            if (parent != null) { // Checks if the file has a parent directory
                Files.createDirectories(parent); // Creates parent directories if needed
            } // End of parent null check

            Files.writeString( // Appends text to the file
                    path, // Uses the current file path
                    text, // Appends the provided text
                    StandardOpenOption.CREATE, // Creates the file if missing
                    StandardOpenOption.APPEND // Appends instead of overwriting
            ); // End of writeString call

            if (cachedContent == null || isCacheStale()) { // Checks whether the cache is missing or outdated
                refreshCache(); // Refreshes the entire cache if needed
            } else { // Runs when the cache is already valid
                cachedContent += text; // Appends the new text to the cached content
                cachedLastModified = lastModifiedOrNull(); // Updates the cached last modified time
            } // End of cache check

            return this; // Returns the current instance for chaining
        } catch (Exception e) { // Catches any exception
            throw new QuickFileException("QuickFile append error: " + e.getMessage(), e); // Wraps it in a custom exception
        } // End of try-catch
    } // End of append

    public QuickFile appendLine(String text) { // Appends text followed by a new line
        return append(text + System.lineSeparator()); // Adds the system line separator and appends
    } // End of appendLine

    public QuickFile appendLines(List<String> lines) { // Appends multiple lines to the file
        String text = String.join(System.lineSeparator(), lines); // Joins the lines into one text block
        if (!text.isEmpty()) { // Checks if the joined text is not empty
            text += System.lineSeparator(); // Adds a trailing new line
        } // End of empty check
        return append(text); // Appends the resulting text block
    } // End of appendLines

    public String read() { // Reads and returns the file content
        try { // Starts protected block for reading
            refreshCache(); // Refreshes the cache before returning content
            return cachedContent; // Returns the cached content
        } catch (Exception e) { // Catches any exception
            throw new QuickFileException("QuickFile read error: " + e.getMessage(), e); // Wraps it in a custom exception
        } // End of try-catch
    } // End of read

    public List<String> readLines() { // Reads all file lines into a list
        try { // Starts protected block for reading lines
            return Files.readAllLines(path); // Reads all lines from the file and returns them
        } catch (Exception e) { // Catches any exception
            throw new QuickFileException("QuickFile readLines error: " + e.getMessage(), e); // Wraps it in a custom exception
        } // End of try-catch
    } // End of readLines

    public String readLine(int index) { // Reads a specific line by index
        List<String> lines = readLines(); // Reads all file lines first

        if (index < 0 || index >= lines.size()) { // Validates the requested index
            throw new QuickFileException("QuickFile readLine error: invalid line index " + index); // Throws an exception for invalid index
        } // End of index validation

        return lines.get(index); // Returns the requested line
    } // End of readLine

    public String firstLine() { // Returns the first line of the file
        List<String> lines = readLines(); // Reads all lines
        return lines.isEmpty() ? "" : lines.get(0); // Returns an empty string if no lines exist, otherwise returns the first line
    } // End of firstLine

    public String lastLine() { // Returns the last line of the file
        List<String> lines = readLines(); // Reads all lines
        return lines.isEmpty() ? "" : lines.get(lines.size() - 1); // Returns an empty string if no lines exist, otherwise returns the last line
    } // End of lastLine

    public List<String> readWords() { // Splits file content into words
        String content = content().trim(); // Gets file content and trims outer whitespace

        if (content.isEmpty()) { // Checks whether the content is empty after trimming
            return List.of(); // Returns an empty immutable list
        } // End of empty check

        return Arrays.stream(content.split("\\s+")) // Splits the content by one or more whitespace characters
                .filter(word -> !word.isBlank()) // Removes any blank word entries
                .collect(Collectors.toList()); // Collects the words into a list
    } // End of readWords

    public String readWord(int index) { // Reads a specific word by index
        List<String> words = readWords(); // Reads all words from the file

        if (index < 0 || index >= words.size()) { // Validates the requested word index
            throw new QuickFileException("QuickFile readWord error: invalid word index " + index); // Throws an exception for invalid index
        } // End of index validation

        return words.get(index); // Returns the requested word
    } // End of readWord

    public String firstWord() { // Returns the first word in the file
        List<String> words = readWords(); // Reads all words
        return words.isEmpty() ? "" : words.get(0); // Returns an empty string if no words exist, otherwise returns the first word
    } // End of firstWord

    public String lastWord() { // Returns the last word in the file
        List<String> words = readWords(); // Reads all words
        return words.isEmpty() ? "" : words.get(words.size() - 1); // Returns an empty string if no words exist, otherwise returns the last word
    } // End of lastWord

    public List<Character> readChars() { // Reads file content as a list of characters
        return content().chars() // Converts the content string into an IntStream of characters
                .mapToObj(c -> (char) c) // Converts each character code into a Character
                .collect(Collectors.toList()); // Collects all characters into a list
    } // End of readChars

    public char readChar(int index) { // Reads a specific character by index
        String content = content(); // Gets the current file content

        if (index < 0 || index >= content.length()) { // Validates the requested character index
            throw new QuickFileException("QuickFile readChar error: invalid char index " + index); // Throws an exception for invalid index
        } // End of index validation

        return content.charAt(index); // Returns the requested character
    } // End of readChar

    public char firstChar() { // Returns the first character in the file
        String content = content(); // Gets the current file content
        return content.isEmpty() ? '\0' : content.charAt(0); // Returns the null character if empty, otherwise the first character
    } // End of firstChar

    public char lastChar() { // Returns the last character in the file
        String content = content(); // Gets the current file content
        return content.isEmpty() ? '\0' : content.charAt(content.length() - 1); // Returns the null character if empty, otherwise the last character
    } // End of lastChar

    public void forEachLine(Consumer<String> action) { // Applies an action to each line in the file
        readLines().forEach(action); // Reads all lines and executes the given action for each line
    } // End of forEachLine

    public void print() { // Prints the current file content to the console
        System.out.println(content()); // Reads the content and prints it
    } // End of print

    public boolean exists() { // Checks whether the file exists
        return Files.exists(path); // Returns true if the file exists
    } // End of exists

    public boolean notExists() { // Checks whether the file does not exist
        return Files.notExists(path); // Returns true if the file does not exist
    } // End of notExists

    public boolean isEmpty() { // Checks whether the file content is empty
        return chars() == 0; // Returns true if the number of characters is zero
    } // End of isEmpty

    public boolean canRead() { // Checks whether the file is readable
        return Files.isReadable(path); // Returns true if the file can be read
    } // End of canRead

    public boolean canWrite() { // Checks whether the file is writable
        return Files.isWritable(path); // Returns true if the file can be written to
    } // End of canWrite

    public boolean isHidden() { // Checks whether the file is hidden
        try { // Starts protected block for hidden-file check
            return Files.isHidden(path); // Returns true if the file is hidden
        } catch (Exception e) { // Catches any exception
            throw new QuickFileException("QuickFile hidden error: " + e.getMessage(), e); // Wraps it in a custom exception
        } // End of try-catch
    } // End of isHidden

    public long size() { // Returns the file size in bytes
        try { // Starts protected block for size lookup
            return Files.exists(path) ? Files.size(path) : 0; // Returns file size if it exists, otherwise zero
        } catch (Exception e) { // Catches any exception
            throw new QuickFileException("QuickFile size error: " + e.getMessage(), e); // Wraps it in a custom exception
        } // End of try-catch
    } // End of size

    public long lines() { // Returns the number of lines in the file
        return content().lines().count(); // Splits the content into lines and counts them
    } // End of lines

    public long words() { // Returns the number of words in the file
        return readWords().size(); // Reads all words and returns the count
    } // End of words

    public long chars() { // Returns the number of characters in the file
        return content().length(); // Returns the length of the content string
    } // End of chars

    public Instant createdAt() { // Returns the file creation timestamp
        try { // Starts protected block for reading file attributes
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class); // Reads file metadata attributes
            return attrs.creationTime().toInstant(); // Converts the creation time to Instant and returns it
        } catch (Exception e) { // Catches any exception
            throw new QuickFileException("QuickFile createdAt error: " + e.getMessage(), e); // Wraps it in a custom exception
        } // End of try-catch
    } // End of createdAt

    public Instant lastModified() { // Returns the last modified timestamp of the file
        try { // Starts protected block for last modified lookup
            return Files.getLastModifiedTime(path).toInstant(); // Reads the last modified time and converts it to Instant
        } catch (Exception e) { // Catches any exception
            throw new QuickFileException("QuickFile lastModified error: " + e.getMessage(), e); // Wraps it in a custom exception
        } // End of try-catch
    } // End of lastModified

    public FileStats stats() { // Calculates and returns detailed file statistics
        String content = content(); // Gets the current file content
        long lines = content.lines().count(); // Counts the number of lines
        long words = readWords().size(); // Counts the number of words
        long chars = content.length(); // Counts the number of characters
        long charsWithoutSpaces = content.replaceAll("\\s+", "").length(); // Counts characters excluding whitespace
        long emptyLines = content.lines().filter(String::isBlank).count(); // Counts blank lines
        long nonEmptyLines = lines - emptyLines; // Calculates non-empty lines
        double averageWordsPerLine = lines == 0 ? 0.0 : (double) words / lines; // Calculates the average words per line safely

        return new FileStats( // Creates and returns a FileStats object
                size(), // Includes file size in bytes
                lines, // Includes total line count
                words, // Includes total word count
                chars, // Includes total character count
                charsWithoutSpaces, // Includes character count without whitespace
                emptyLines, // Includes number of blank lines
                nonEmptyLines, // Includes number of non-blank lines
                averageWordsPerLine // Includes average number of words per line
        ); // End of FileStats construction
    } // End of stats

    public boolean contains(String text) { // Checks whether the file content contains the given text
        return content().contains(text); // Returns true if the content contains the text
    } // End of contains

    public boolean startsWith(String text) { // Checks whether the file content starts with the given text
        return content().startsWith(text); // Returns true if the content starts with the text
    } // End of startsWith

    public boolean endsWith(String text) { // Checks whether the file content ends with the given text
        return content().endsWith(text); // Returns true if the content ends with the text
    } // End of endsWith

    public boolean matches(String regex) { // Checks whether the entire file content matches a regex pattern
        return Pattern.compile(regex, Pattern.DOTALL) // Compiles the pattern with DOTALL so dots can match new lines
                .matcher(content()) // Applies the pattern to the current content
                .matches(); // Returns true if the full content matches
    } // End of matches

    public List<String> findByRegex(String regex) { // Finds all matches of a regex inside the file content
        try { // Starts protected block for regex matching
            Pattern pattern = Pattern.compile(regex); // Compiles the provided regex pattern
            Matcher matcher = pattern.matcher(content()); // Creates a matcher against the current content
            List<String> matches = new java.util.ArrayList<>(); // Creates a list to store all matched results

            while (matcher.find()) { // Iterates over all regex matches
                matches.add(matcher.group()); // Adds each matched group to the result list
            } // End of match loop

            return matches; // Returns the list of matches
        } catch (Exception e) { // Catches any exception
            throw new QuickFileException("QuickFile regex error: " + e.getMessage(), e); // Wraps it in a custom exception
        } // End of try-catch
    } // End of findByRegex

    public long count(String text) { // Counts how many times a text appears in the file content
        if (text == null) { // Checks whether the target text is null
            throw new QuickFileException("QuickFile count error: text cannot be null"); // Throws an exception if the text is null
        } // End of null check

        if (text.isEmpty()) { // Checks whether the target text is empty
            return 0; // Returns zero because counting empty strings is not meaningful here
        } // End of empty check

        String content = content(); // Gets the current file content
        long count = 0; // Initializes the match counter
        int index = 0; // Initializes the current search position

        while ((index = content.indexOf(text, index)) != -1) { // Repeatedly searches for the text from the current position
            count++; // Increments the match counter for each occurrence
            index += text.length(); // Moves the search position forward past the matched text
        } // End of search loop

        return count; // Returns the total number of matches
    } // End of count

    public List<String> linesContaining(String text) { // Returns all lines that contain the given text
        return content() // Gets the current file content
                .lines() // Converts the content into a stream of lines
                .filter(line -> line.contains(text)) // Keeps only lines that contain the target text
                .collect(Collectors.toList()); // Collects the matching lines into a list
    } // End of linesContaining

    public QuickFile replace(String oldText, String newText) { // Replaces all exact text occurrences and writes the result back
        return write(content().replace(oldText, newText)); // Replaces text in memory and writes the new content
    } // End of replace

    public QuickFile replaceAll(String regex, String replacement) { // Replaces all regex matches and writes the result back
        return write(content().replaceAll(regex, replacement)); // Performs regex replacement and writes the new content
    } // End of replaceAll

    public QuickFile clear() { // Clears the file content
        return write(""); // Writes an empty string to the file
    } // End of clear

    public QuickFile changeExtension(String newExtension) { // Changes the file extension safely
        String cleanExtension = newExtension == null ? "" : newExtension.trim(); // Normalizes the new extension and handles null

        if (cleanExtension.startsWith(".")) { // Checks whether the extension starts with a dot
            cleanExtension = cleanExtension.substring(1); // Removes the leading dot
        } // End of dot cleanup

        String fullName = fullName(); // Gets the current full file name

        if (fullName.isEmpty()) { // Checks whether the file name is empty
            throw new QuickFileException("QuickFile changeExtension error: file name cannot be empty"); // Throws an exception if empty
        } // End of empty file name check

        if (fullName.startsWith(".") && fullName.indexOf('.', 1) == -1) { // Checks whether the file is a hidden file with no safe extension segment
            throw new QuickFileException("QuickFile changeExtension error: cannot safely change extension of hidden file: " + fullName); // Throws an exception to avoid unsafe rename
        } // End of hidden file check

        String baseName = name(); // Gets the file name without extension

        if (baseName.isEmpty()) { // Checks whether the base name is empty
            throw new QuickFileException("QuickFile changeExtension error: file name cannot be empty"); // Throws an exception if empty
        } // End of empty base name check

        if (cleanExtension.isEmpty()) { // Checks whether the new extension is empty
            return rename(baseName); // Renames the file to just the base name without an extension
        } // End of empty extension check

        return rename(baseName + "." + cleanExtension); // Renames the file using the new extension
    } // End of changeExtension

    public QuickFile delete() { // Deletes the file if it exists
        try { // Starts protected block for deletion
            Files.deleteIfExists(path); // Deletes the file if present
            cachedContent = null; // Clears the cached content
            cachedLastModified = null; // Clears the cached timestamp
            return this; // Returns the current instance for chaining
        } catch (Exception e) { // Catches any exception
            throw new QuickFileException("QuickFile delete error: " + e.getMessage(), e); // Wraps it in a custom exception
        } // End of try-catch
    } // End of delete

    public QuickFile copy(String target) { // Copies the file to the given string path
        return copyTo(target); // Delegates to copyTo
    } // End of copy(String)

    public QuickFile copy(QuickPath target) { // Copies the file to the given QuickPath
        return copyTo(target.toString()); // Converts QuickPath to string and delegates to copyTo
    } // End of copy(QuickPath)

    public QuickFile copyTo(String target) { // Copies the file to another location
        try { // Starts protected block for copy operation
            Path targetPath = Paths.get(target); // Converts the target string into a Path
            Path parent = targetPath.getParent(); // Gets the parent directory of the target

            if (parent != null) { // Checks whether the target has a parent directory
                Files.createDirectories(parent); // Creates parent directories if needed
            } // End of parent null check

            Files.copy(path, targetPath, StandardCopyOption.REPLACE_EXISTING); // Copies the file and replaces the target if it already exists
            return this; // Returns the current instance for chaining
        } catch (Exception e) { // Catches any exception
            throw new QuickFileException("QuickFile copy error: " + e.getMessage(), e); // Wraps it in a custom exception
        } // End of try-catch
    } // End of copyTo

    public QuickFile move(String target) { // Moves the file to the given string path
        return moveTo(target); // Delegates to moveTo
    } // End of move(String)

    public QuickFile move(QuickPath target) { // Moves the file to the given QuickPath
        return moveTo(target.toString()); // Converts QuickPath to string and delegates to moveTo
    } // End of move(QuickPath)

    public QuickFile moveTo(String target) { // Moves the file to another location
        try { // Starts protected block for move operation
            Path targetPath = Paths.get(target); // Converts the target string into a Path
            Path parent = targetPath.getParent(); // Gets the parent directory of the target

            if (parent != null) { // Checks whether the target has a parent directory
                Files.createDirectories(parent); // Creates parent directories if needed
            } // End of parent null check

            Files.move(path, targetPath, StandardCopyOption.REPLACE_EXISTING); // Moves the file and replaces the target if it exists
            this.path = targetPath; // Updates the internal path to the new location
            cachedContent = null; // Clears the cached content because the file path changed
            cachedLastModified = null; // Clears the cached timestamp because the file path changed
            return this; // Returns the current instance for chaining
        } catch (Exception e) { // Catches any exception
            throw new QuickFileException("QuickFile move error: " + e.getMessage(), e); // Wraps it in a custom exception
        } // End of try-catch
    } // End of moveTo

    public QuickFile rename(String newName) { // Renames the file within the same directory
        try { // Starts protected block for rename operation
            Path targetPath = path.resolveSibling(newName); // Builds a target path in the same directory with the new name
            Files.move(path, targetPath, StandardCopyOption.REPLACE_EXISTING); // Renames the file by moving it to the sibling path
            this.path = targetPath; // Updates the internal path to the renamed file
            cachedContent = null; // Clears the cached content because the file identity changed
            cachedLastModified = null; // Clears the cached timestamp because the file identity changed
            return this; // Returns the current instance for chaining
        } catch (Exception e) { // Catches any exception
            throw new QuickFileException("QuickFile rename error: " + e.getMessage(), e); // Wraps it in a custom exception
        } // End of try-catch
    } // End of rename

    public String name() { // Returns the file name without its extension
        String fullName = fullName(); // Gets the complete file name
        int dotIndex = fullName.lastIndexOf('.'); // Finds the last dot in the name

        if (fullName.startsWith(".") && dotIndex == 0) { // Checks whether the file is a hidden file like .env
            return fullName; // Returns the full name unchanged for hidden files without a normal base name
        } // End of hidden-file check

        return dotIndex > 0 ? fullName.substring(0, dotIndex) : fullName; // Returns the name without extension if one exists
    } // End of name

    public String fullName() { // Returns the full file name including extension
        Path fileName = path.getFileName(); // Gets the file name part from the path
        return fileName == null ? "" : fileName.toString(); // Returns an empty string if no file name exists, otherwise the file name
    } // End of fullName

    public String extension() { // Returns the file extension without the dot
        String fullName = fullName(); // Gets the complete file name
        int dotIndex = fullName.lastIndexOf('.'); // Finds the last dot in the name

        if (fullName.startsWith(".") && dotIndex == 0) { // Checks whether the file is a hidden file like .env
            return ""; // Returns an empty extension for hidden files without a normal extension
        } // End of hidden-file check

        return dotIndex > 0 ? fullName.substring(dotIndex + 1) : ""; // Returns the extension if one exists, otherwise an empty string
    } // End of extension

    public String parent() { // Returns the parent directory path as a string
        Path parent = path.getParent(); // Gets the parent path
        return parent == null ? "" : parent.toString(); // Returns an empty string if there is no parent, otherwise the parent path
    } // End of parent

    private String content() { // Returns cached content and refreshes it if needed
        try { // Starts protected block for content access
            if (cachedContent == null || isCacheStale()) { // Checks whether the cache is missing or outdated
                refreshCache(); // Reloads the file content into the cache
            } // End of cache validation
            return cachedContent; // Returns the cached content
        } catch (Exception e) { // Catches any exception
            throw new QuickFileException("QuickFile content error: " + e.getMessage(), e); // Wraps it in a custom exception
        } // End of try-catch
    } // End of content

    private void refreshCache() { // Reloads the file content and timestamp into the cache
        try { // Starts protected block for cache refresh
            cachedContent = Files.readString(path); // Reads the file content into the cache
            cachedLastModified = lastModifiedOrNull(); // Updates the cached last modified time
        } catch (Exception e) { // Catches any exception
            throw new QuickFileException("QuickFile cache error: " + e.getMessage(), e); // Wraps it in a custom exception
        } // End of try-catch
    } // End of refreshCache

    private boolean isCacheStale() { // Checks whether the cached content is outdated
        Instant currentLastModified = lastModifiedOrNull(); // Reads the current last modified time from the file system
        return cachedLastModified == null || !cachedLastModified.equals(currentLastModified); // Returns true if the cache has no timestamp or timestamps do not match
    } // End of isCacheStale

    private Instant lastModifiedOrNull() { // Returns the last modified time if the file exists, otherwise null
        try { // Starts protected block for last modified lookup
            return Files.exists(path) ? Files.getLastModifiedTime(path).toInstant() : null; // Returns the last modified time or null if the file does not exist
        } catch (Exception e) { // Catches any exception
            throw new QuickFileException("QuickFile lastModified error: " + e.getMessage(), e); // Wraps it in a custom exception
        } // End of try-catch
    } // End of lastModifiedOrNull

    public static class QuickFileException extends RuntimeException { // Declares a custom runtime exception for QuickFile errors
        public QuickFileException(String message) { // Constructor that accepts an error message
            super(message); // Passes the message to the parent RuntimeException
        } // End of QuickFileException(String)

        public QuickFileException(String message, Throwable cause) { // Constructor that accepts an error message and original cause
            super(message, cause); // Passes both the message and cause to the parent RuntimeException
        } // End of QuickFileException(String, Throwable)
    } // End of QuickFileException class
} // End of QuickFile class
