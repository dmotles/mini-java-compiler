package visitor.symbol;

import syntaxtree.*;

public class Label extends Symbol {
	int index;

	public Label() {}

	public Label(int i) {
		index = i;
		name = new Identifier(String.valueOf(i));
	}

	public String toString() {
		return name.s;
	}

	public void backpatch(int i) {
		index = i;
		name = new Identifier(String.valueOf(i));
	}
}