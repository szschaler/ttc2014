package uk.ac.kcl.inf.smal.ttc2014.ant.compositionrules;

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
	
	@Override
	public boolean areEqual(FSTTerminal terminalA, FSTTerminal terminalB) {
		System.out.println("areEqual called.");
		return false;
	}

	@Override
	public void compose(FSTTerminal terminalA, FSTTerminal terminalB,
			FSTTerminal terminalComp, FSTNonTerminal nonterminalParent) {
		System.out.println("Composing two fields: " + terminalA.getBody() + " << >> " + terminalB.getBody());
		
		super.compose(terminalA, terminalB, terminalComp, nonterminalParent);
	}

}
