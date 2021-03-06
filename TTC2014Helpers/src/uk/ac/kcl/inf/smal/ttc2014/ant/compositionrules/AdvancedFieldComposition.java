package uk.ac.kcl.inf.smal.ttc2014.ant.compositionrules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.epsilon.egl.symmetric_ao.tasks.superimpose.rules.FieldOverrideRule;

import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

/**
 * Special composition rule for class fields. This rule is able to compose
 * fields of different type. In particular, it can compose fields of type T with
 * fields of type List<T> into a field of type List<T>.
 * 
 * @author Steffen Zschaler
 * 
 */
public class AdvancedFieldComposition extends FieldOverrideRule {

	/**
	 * Pattern for matching the modifiers, (first) name, and type of a field
	 * declaration.
	 */
	private static Pattern pModifiers = Pattern.compile("^[^,;=]+");

	/**
	 * Extract the field type from a given field declaration terminal.
	 * 
	 * @param t
	 * @return
	 */
	private String getType(FSTTerminal t) {
		String sBody = t.getBody();

		Matcher m = pModifiers.matcher(sBody);
		if (m.find()) {
			String[] saModifiers = m.group().split("\\s+");

			// The last but one word should be the type of the field, assuming
			// we're dealing with syntactically correct code here
			return saModifiers[saModifiers.length - 2];
		} else {
			return null;
		}
	}

	/**
	 * Set the type of the given field declaration. Also removes assignments, if
	 * any.
	 * 
	 * @param t
	 * @param newType
	 */
	private void setType(FSTTerminal t, String newType) {
		String sBody = t.getBody();

		Matcher m = pModifiers.matcher(sBody);
		if (m.find()) {
			String[] saModifiers = m.group().split("\\s+");

			// The last but one word should be the type of the field, assuming
			// we're dealing with syntactically correct code here
			saModifiers[saModifiers.length - 2] = newType;

			// TODO: Switch to Java8 and use set stream API
			String sRecombined = "";
			for (String sModifier : saModifiers) {
				sRecombined += sModifier;
			}

			StringBuffer sbResult = new StringBuffer();
			m.appendReplacement(sbResult, sRecombined).appendTail(sbResult);

			// Strip out any assignments and set body of field declaration
			// t.setBody(sbResult.toString().replaceAll("=.+$", ";"));
			t.setBody(sbResult.toString());
		}
	}

	/**
	 * Pattern for matching the assignment part of the field declaration, if
	 * any.
	 */
	private static Pattern pAssignment = Pattern.compile("=\\s*(.+)\\s*;\\s*$");

	/**
	 * Extract the value part of the field, if any.
	 * 
	 * @param t
	 * @return
	 */
	private String getValue(FSTTerminal t) {
		Matcher m = pAssignment.matcher(t.getBody());
		if (m.find()) {
			return m.group();
		} else {
			return null;
		}
	}

	/**
	 * Replace any assignment by the value given in the parameter.
	 * 
	 * @param t
	 * @param sNewValue
	 */
	private void setValue(FSTTerminal t, String sNewValue) {
		Matcher m = pAssignment.matcher(t.getBody());
		if (m.find()) {
			StringBuffer sbResult = new StringBuffer();
			m.appendReplacement(sbResult, sNewValue).appendTail(sbResult);
			t.setBody(sbResult.toString());
		} else {
			t.setBody(t.getBody().replaceAll(";$", " = " + sNewValue + ";"));
		}
	}

	@Override
	public void compose(FSTTerminal terminalA, FSTTerminal terminalB,
			FSTTerminal terminalComp, FSTNonTerminal nonterminalParent) {
		// System.out.println("AdvancedFieldComposition.compose()");
		try {
			String typeA = getType(terminalA);
			String typeB = getType(terminalB);

			// System.out.println("Merging fields: " + typeA + ", " + typeB +
			// ".");

			if (!typeA.equals(typeB)) {
				// Merge types if possible
				if ((typeA.equals("List"))
						// generics aren't actually supported by our Java
						// grammar
						|| (typeA.equals("List<" + typeB + ">"))
						|| (typeA.equals(typeB + "[]"))) {
					setType(terminalB, typeA);
					setValue(terminalB, getValue(terminalA));
				} else if ((typeB.equals("List"))
						// generics aren't actually supported by our Java
						// grammar
						|| (typeB.equals("List<" + typeA + ">"))
						|| (typeB.equals(typeA + "[]"))) {
					setType(terminalA, typeB);
					setValue(terminalA, getValue(terminalB));
				} else {
					System.err.println("Cannot currently unify these types: "
							+ typeA + ", " + typeB);
					throw new UnsupportedOperationException(
							"Cannot currently unify these types: " + typeA
									+ ", " + typeB);
				}
			}

			super.compose(terminalA, terminalB, terminalComp, nonterminalParent);
		} catch (Throwable t) {
			System.out.println(t.getMessage());
			t.printStackTrace();
			throw t;
		}
	}

}
