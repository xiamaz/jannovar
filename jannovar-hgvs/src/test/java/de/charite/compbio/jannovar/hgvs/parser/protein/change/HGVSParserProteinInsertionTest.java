package de.charite.compbio.jannovar.hgvs.parser.protein.change;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Aa_change_insertionContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

/**
 * Parser for HGVS deletion amino acid changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserProteinInsertionTest extends HGVSParserTestBase {

	@Test
	public void testInsertionWithoutSequenceTest() {
		Antlr4HGVSParser parser = buildParserForString("Cys123_Ala124ins", Antlr4HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_insertionContext aa_change_insertion = parser.aa_change_insertion();
		Assert.assertEquals(
				"(aa_change_insertion (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Ala) 124)) ins)",
				aa_change_insertion.toStringTree(parser));
	}

	@Test
	public void testInsertionWithSequenceTest() {
		Antlr4HGVSParser parser = buildParserForString("Cys123_Ala124insThrGlu", Antlr4HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_insertionContext aa_change_insertion = parser.aa_change_insertion();
		Assert.assertEquals(
				"(aa_change_insertion (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Ala) 124)) ins (aa_string Thr Glu))",
				aa_change_insertion.toStringTree(parser));
	}

	@Test
	public void testInsertionWithLengthTest() {
		Antlr4HGVSParser parser = buildParserForString("Cys123_Ala124ins2", Antlr4HGVSLexer.PROTEIN_CHANGE, false);
		Aa_change_insertionContext aa_change_insertion = parser.aa_change_insertion();
		Assert.assertEquals(
				"(aa_change_insertion (aa_range (aa_point_location (aa_char Cys) 123) _ (aa_point_location (aa_char Ala) 124)) ins 2)",
				aa_change_insertion.toStringTree(parser));
	}

}
