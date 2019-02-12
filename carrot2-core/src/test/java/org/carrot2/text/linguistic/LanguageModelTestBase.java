
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic;

import org.carrot2.core.LanguageCode;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.util.MutableCharArray;
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Superclass for testing {@link LanguageModel}s.
 */
public abstract class LanguageModelTestBase extends CarrotTestCase
{
    /**
     * 
     */
    protected LanguageModel languageModel;

    /**
     * @return Returns language code for this test.
     */
    protected abstract LanguageCode getLanguageCode();

    /**
     * 
     */
    @Before
    public void setupLanguage()
    {
        LanguageCode langCode = getLanguageCode();

        IStemmer stemmer = new DefaultStemmerFactory().getStemmer(langCode);
        ITokenizer tokenizer = new DefaultTokenizerFactory().getTokenizer(langCode);
        ILexicalData lexicalData = new DefaultLexicalDataFactory().getLexicalData(langCode);

        this.languageModel = new LanguageModel(stemmer, tokenizer, lexicalData);
    }

    /**
     * 
     */
    @Test
    public void testStemmerAvailable()
    {
        assertNotNull(languageModel.stemmer);
        assertThat(languageModel.stemmer.getClass()).as("Stemmer class for: " + getLanguageCode())
            .isNotEqualTo(IdentityStemmer.class);
    }

    /**
     * 
     */
    @Test
    public void testStemming()
    {
        final String [][] testData = getStemmingTestData();
        final IStemmer stemmer = languageModel.stemmer;

        for (String [] pair : testData)
        {
            CharSequence stemmed = stemmer.stem(pair[0]);
            assertEquals("Stemming difference: " + pair[0] + " should become " + pair[1]
                + " but was transformed into " + stemmed, pair[1], stemmed == null ? null
                : stemmed.toString());
        }
    }

    /**
     * 
     */
    @Test
    public void testCommonWords()
    {
        final String [] testData = getCommonWordsTestData();
        for (String word : testData)
        {
            assertTrue(languageModel.lexicalData.isCommonWord(
                new MutableCharArray(word)));
        }
    }

    /**
     * Override and provide word pairs for {@link LanguageModel#stemmer} tests.
     * Sample data should follow this format:
     * 
     * <pre>
     * return new String [] []
     * {
     *     {
     *         &quot;inflected&quot;, &quot;base&quot;
     *     },
     *     {
     *         &quot;inflected&quot;, &quot;base&quot;
     *     },
     * };
     * </pre>
     */
    protected String [][] getStemmingTestData()
    {
        return new String [] [] {
        /* Empty by default. */
        };
    }

    /**
     * Override and provide words for testing against
     * {@link ILexicalData#isCommonWord(MutableCharArray)}).
     */
    protected String [] getCommonWordsTestData()
    {
        return new String [] {
        /* Empty by default. */
        };
    }

}