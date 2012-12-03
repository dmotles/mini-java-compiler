package syntaxtree;

public abstract class ASTNode  {
	private int lineNumber = -1;
	private int charNumber = -1;

	public void setPosition(int l, int c) {
		lineNumber = l;
		charNumber = c;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public int getCharNumber() {
		return charNumber;
	}
}
