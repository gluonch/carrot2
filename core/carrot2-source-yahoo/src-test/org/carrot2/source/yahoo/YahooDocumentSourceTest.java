package org.carrot2.source.yahoo;

import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.*;

import org.apache.commons.lang.StringEscapeUtils;
import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.test.DocumentSourceTestBase;
import org.carrot2.source.SearchMode;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junitext.Prerequisite;
import org.junitext.runners.AnnotationRunner;

import com.google.common.collect.*;

/**
 * Tests Yahoo! input component.
 */
@RunWith(AnnotationRunner.class)
public class YahooDocumentSourceTest extends DocumentSourceTestBase<YahooDocumentSource>
{
    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testNoResultsQuery() throws Exception
    {
        assertEquals(0, runQuery("duiogig oiudgisugviw siug iugw iusviuwg", 100));
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testQueryLargerThanPage() throws Exception
    {
        final int needed = new YahooWebSearchService().resultsPerPage * 2 + 10;

        // Allow some slack (duplicated URLs).
        final int documentsReturned = runQuery("apache", needed);

        assertTrue(documentsReturned > needed - 5);
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testResultsTotal() throws Exception
    {
        runQuery("apache", 50);

        assertNotNull(processingAttributes.get(AttributeNames.RESULTS_TOTAL));
        assertTrue((Long) processingAttributes.get(AttributeNames.RESULTS_TOTAL) > 0);
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    @SuppressWarnings("unchecked")
    public void testURLsUnique() throws Exception
    {
        runQuery("apache", 200);

        assertFieldUnique((Collection<Document>) processingAttributes
            .get(AttributeNames.DOCUMENTS), Document.CONTENT_URL);
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testConservativeMode() throws Exception
    {
        processingAttributes.put("search-mode", SearchMode.CONSERVATIVE);

        assertEquals(0, runQuery("duiogig oiudgisugviw siug iugw iusviuwg", 100));
        assertEquals(1, processingAttributes.get(YahooSearchService.class.getName()
            + ".requestCount"));
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testSpeculativeMode() throws Exception
    {
        processingAttributes.put("search-mode", SearchMode.SPECULATIVE);

        assertEquals(0, runQuery("duiogig oiudgisugviw siug iugw iusviuwg", 100));
        assertEquals(2, processingAttributes.get(YahooSearchService.class.getName()
            + ".requestCount"));
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    public void testNewsServiceSearch() throws Exception
    {
        processingAttributes.put(YahooDocumentSource.class.getName() + ".service",
            YahooNewsSearchService.class);

        assertTrue(runQuery("iraq", 50) > 0);
    }

    @Ignore
    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    @SuppressWarnings("unchecked")
    public void dumpResultAsJavaSource() throws Exception
    {
        runQuery("data mining", 100);

        Collection<Document> docs = (Collection<Document>) processingAttributes
            .get(AttributeNames.DOCUMENTS);
        for (Document d : docs)
        {
            System.out.println("{ \""
                + StringEscapeUtils.escapeJava((String) d.getField(Document.CONTENT_URL))
                + "\",\n\t"
                + "\"" + StringEscapeUtils.escapeJava((String) d.getField(Document.TITLE)) 
                + "\",\n\t"
                + "\"" + StringEscapeUtils.escapeJava((String) d.getField(Document.SUMMARY))
                + "\" },\n\n");
        }
    }

    @Test
    @Prerequisite(requires = "externalApiTestsEnabled")
    @SuppressWarnings("unchecked")
    public void testInCachingController() throws InterruptedException, ExecutionException
    {
        final Map<String, Object> attributes = Maps.newHashMap();
        attributes.put(AttributeNames.QUERY, "test");
        attributes.put(AttributeNames.RESULTS, 50);

        // Cache results from all DataSources
        final CachingController cachingController = new CachingController(
            DocumentSource.class);
        cachingController.init(new HashMap<String, Object>());

        int count = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        List<Callable<ProcessingResult>> callables = Lists.newArrayList();
        for (int i = 0; i < count; i++)
        {
            callables.add(new Callable<ProcessingResult>()
            {
                public ProcessingResult call() throws Exception
                {
                    Map<String, Object> localAttributes = Maps.newHashMap(attributes);
                    return cachingController.process(localAttributes,
                        YahooDocumentSource.class);
                }
            });
        }

        List<Future<ProcessingResult>> results = executorService.invokeAll(callables);

        List<Document> documents = null;
        for (Future<ProcessingResult> future : results)
        {
            ProcessingResult processingResult = future.get();
            final List<Document> documentsLocal = (List<Document>) processingResult
                .getAttributes().get(AttributeNames.DOCUMENTS);
            assertNotNull(documentsLocal);
            assertTrue(documentsLocal.size() <= 50 && documentsLocal.size() >= 35);

            // Should have same documents (from the cache)
            if (documents != null)
            {
                for (int i = 0; i < documents.size(); i++)
                {
                    assertSame(documents.get(i), documentsLocal.get(i));
                }
            }
            documents = documentsLocal;
        }
    }

    @Override
    public Class<YahooDocumentSource> getComponentClass()
    {
        return YahooDocumentSource.class;
    }
}
