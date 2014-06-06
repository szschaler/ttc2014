package uk.ac.kcl.inf.smal.ttc2014.ant.compositionrules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.epsilon.egl.symmetric_ao.tasks.superimpose.rules.FieldOverrideRule;

import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

/**
 * Special composition rule for class fields. This rule is able to compose
 * fields of different type. In particular, it can compose fields of type T with
 * fields of type Set<T> into a field of type Set<T>.
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
		if (m.matches()) {
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
		if (m.matches()) {
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
			t.setBody(sbResult.toString().replaceAll("=[^;]+;", ";"));
		}
	}

	@Override
	public void compose(FSTTerminal terminalA, FSTTerminal terminalB,
			FSTTerminal terminalComp, FSTNonTerminal nonterminalParent) {

		String typeA = getType(terminalA);
		String typeB = getType(terminalB);

		if (!typeA.equals(typeB)) {
			// Merge types if possible
			if (typeA.equals("Set<" + typeB + ">")) {
				setType(terminalB, typeA);
			} else if (typeB.equals("Set<" + typeA + ">")) {
				setType(terminalA, typeB);
			} else {
				throw new UnsupportedOperationException(
						"Cannot currently unify these types: " + typeA + ", "
								+ typeB);
			}
		}

		super.compose(terminalA, terminalB, terminalComp, nonterminalParent);
	}

}
