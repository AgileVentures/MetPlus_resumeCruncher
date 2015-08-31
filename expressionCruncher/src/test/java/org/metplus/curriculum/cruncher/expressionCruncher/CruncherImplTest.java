package org.metplus.curriculum.cruncher.expressionCruncher;



import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class CruncherImplTest {
    //private final String ipsilum = "Lorem ipsum ad his scripta blandit partiendo, eum fastidii accumsan euripidis in, eum liber hendrerit an. Qui ut wisi vocibus suscipiantur, quo dicit ridens inciderint id. Quo mundi lobortis reformidans eu, legimus senserit definiebas an eos. Eu sit tincidunt incorrupte definitionem, vis mutat affert percipit cu, eirmod consectetuer signiferumque eu per. In usu latine equidem dolores. Quo no falli viris intellegam, ut fugit veritus placerat per.\n";
    @Test
    public void testSetMergeList() throws Exception {
        CruncherImpl cnt = new CruncherImpl();
        Hashtable<String, List<String>> mergeList = new Hashtable<>();
        mergeList.put("bamm", new ArrayList<>());
        mergeList.put("bamm1", new ArrayList<>());

        mergeList.get("bamm").add("asd");
        mergeList.get("bamm").add("asd2");
        mergeList.get("bamm").add("asd3");
        mergeList.get("bamm1").add("asd1");

        cnt.setMergeList(mergeList);
        Assert.assertEquals(mergeList, cnt.getMergeList());
        mergeList.put("new line", new ArrayList<>());
        cnt.setMergeList(mergeList);

        Hashtable retVal = cnt.getMergeList();
        Assert.assertNotEquals(mergeList, retVal);

        Assert.assertTrue(null != retVal.get("new@@@@@line"));
    }

    @Test
    public void testGetResult() throws Exception {
        CruncherImpl cnt = new CruncherImpl();
        Hashtable<String, Integer> retVal = new Hashtable<>();
        Assert.assertEquals(retVal, cnt.getResult());
        retVal.put("Bamm", 1);
        cnt.setCaseSensitive(true);
        cnt.calculate("Bamm");
        Assert.assertEquals(retVal, cnt.getResult());
    }

    @Test
    public void testCalculateSimpleExampleCaseSensitive() throws Exception {
        CruncherImpl cnt = new CruncherImpl();
        String text = "Lorem ipsum ad his scripta blandit partiendo. Scripta ad His.";
        cnt.setCaseSensitive(true);
        Hashtable<String, Integer> retVal;
        retVal = cnt.calculate(text);
        Assert.assertEquals(2, retVal.get("ad").intValue());
        Assert.assertEquals(1, retVal.get("his").intValue());
        Assert.assertEquals(1, retVal.get("Scripta").intValue());
    }
    @Test
    public void testCalculateSimpleExampleCaseInsensitive() throws Exception {
        CruncherImpl cnt = new CruncherImpl();
        String text = "Lorem ipsum ad his scripta blandit partiendo. Scripta ad His.";
        Hashtable<String, Integer> retVal;
        cnt.setCaseSensitive(false);
        retVal = cnt.calculate(text);

        Assert.assertEquals(2, retVal.get("ad").intValue());
        Assert.assertEquals(2, retVal.get("his").intValue());
        Assert.assertNull(retVal.get("Scripta"));
        Assert.assertEquals(2, retVal.get("scripta").intValue());
        Assert.assertEquals(1, retVal.get("partiendo").intValue());
    }


    @Test
    public void testCalculateWithIgnoreListExampleCaseSensitive() throws Exception {
        ArrayList<String> ignoreList = new ArrayList<>();
        ignoreList.add("His");
        ignoreList.add(" \\wd ");
        ignoreList.add("\\s*L.+sum\\s");
        CruncherImpl cnt = new CruncherImpl(ignoreList);
        String text = "Lorem ipsum ad his scripta blandit partiendo. Scripta ad His.";
        Hashtable<String, Integer> retVal;
        cnt.setCaseSensitive(true);
        retVal = cnt.calculate(text);
        Assert.assertNull(retVal.get("ad"));
        Assert.assertNull(retVal.get("Lorem"));
        Assert.assertEquals(1, retVal.get("his").intValue());
        Assert.assertEquals(1, retVal.get("Scripta").intValue());
    }
    @Test
    public void testCalculateWithIgnoreListExampleCaseInsensitive() throws Exception {
        ArrayList<String> ignoreList = new ArrayList<>();
        ignoreList.add("His");
        ignoreList.add(" \\wd ");
        ignoreList.add("\\s*L.+sum\\s");
        CruncherImpl cnt = new CruncherImpl(ignoreList);
        String text = "Lorem ipsum ad his scripta blandit partiendo. Scripta ad His.";
        Hashtable<String, Integer> retVal;
        cnt.setCaseSensitive(false);
        retVal = cnt.calculate(text);

        Assert.assertEquals(1, retVal.get("his").intValue());
        Assert.assertNull(retVal.get("ad"));
        Assert.assertNull(retVal.get("lorem"));
        Assert.assertNull(retVal.get("Scripta"));
        Assert.assertEquals(2, retVal.get("scripta").intValue());
        Assert.assertEquals(1, retVal.get("partiendo").intValue());
    }

    @Test
    public void testCalculateWithMergeListExampleCaseSensitive() throws Exception {
        Hashtable<String, List<String>> mergeList = new Hashtable<>();
        ArrayList<String> singleList = new ArrayList<>();
        singleList.add("\\s*L.+sum\\s");
        singleList.add("\\sscripta blandit\\s");
        mergeList.put("Bamba", singleList);
        CruncherImpl cnt = new CruncherImpl(mergeList);
        String text = "Lorem ipsum ad his scripta blandit partiendo. Scripta ad His.";
        Hashtable<String, Integer> retVal;
        cnt.setCaseSensitive(true);
        retVal = cnt.calculate(text);
        Assert.assertEquals(2, retVal.get("ad").intValue());
        Assert.assertNull(retVal.get("Lorem"));
        Assert.assertEquals(1, retVal.get("his").intValue());
        Assert.assertEquals(1, retVal.get("Scripta").intValue());
        Assert.assertEquals(2, retVal.get("Bamba").intValue());
    }
    @Test
    public void testCalculateWithMergeListExampleCaseInsensitive() throws Exception {
        Hashtable<String, List<String>> mergeList = new Hashtable<>();
        ArrayList<String> singleList = new ArrayList<>();
        singleList.add("\\s*L.+sum\\s");
        singleList.add("\\sscripta blandit\\s");
        mergeList.put("Bamba", singleList);
        CruncherImpl cnt = new CruncherImpl(mergeList);
        String text = "Lorem ipsum ad his scripta blandit partiendo. Scripta ad His.";
        Hashtable<String, Integer> retVal;
        cnt.setCaseSensitive(false);
        retVal = cnt.calculate(text);

        Assert.assertEquals(2, retVal.get("his").intValue());
        Assert.assertEquals(2, retVal.get("ad").intValue());
        Assert.assertEquals(1, retVal.get("lorem").intValue());
        Assert.assertNull(retVal.get("Scripta"));
        Assert.assertEquals(1, retVal.get("scripta").intValue());
        Assert.assertEquals(1, retVal.get("partiendo").intValue());
        Assert.assertEquals(1, retVal.get("Bamba").intValue());
    }
    @Test
    public void testCalculateWithMergeToExpressionExample() throws Exception {
        Hashtable<String, List<String>> mergeList = new Hashtable<>();
        ArrayList<String> singleList = new ArrayList<>();
        singleList.add("\\s*L.+sum\\s");
        singleList.add("\\sscripta blandit\\s");
        mergeList.put("My master script", singleList);
        CruncherImpl cnt = new CruncherImpl(mergeList);
        String text = "Lorem ipsum ad his scripta blandit partiendo. Scripta ad His.";
        Hashtable<String, Integer> retVal;
        cnt.setCaseSensitive(true);
        retVal = cnt.calculate(text);
        Assert.assertEquals(2, retVal.get("ad").intValue());
        Assert.assertNull(retVal.get("Lorem"));
        Assert.assertEquals(1, retVal.get("his").intValue());
        Assert.assertEquals(1, retVal.get("Scripta").intValue());
        Assert.assertEquals(2, retVal.get("My master script").intValue());
    }

    @Test
    public void testCalculateWithMergeListWithIgnoreListExampleCaseSensitive() throws Exception {
        Hashtable<String, List<String>> mergeList = new Hashtable<>();
        ArrayList<String> singleList = new ArrayList<>();
        singleList.add("\\s*L.+sum\\s");
        singleList.add("\\sscripta blandit\\s");
        mergeList.put("Bamba", singleList);

        ArrayList<String> ignoreList = new ArrayList<>();
        ignoreList.add("Lorem");

        CruncherImpl cnt = new CruncherImpl(ignoreList, mergeList);
        String text = "Lorem ipsum ad his scripta blandit partiendo. Scripta ad His.";
        Hashtable<String, Integer> retVal;
        cnt.setCaseSensitive(true);
        retVal = cnt.calculate(text);
        Assert.assertEquals(2, retVal.get("ad").intValue());
        Assert.assertNull(retVal.get("Lorem"));
        Assert.assertEquals(1, retVal.get("his").intValue());
        Assert.assertEquals(1, retVal.get("Scripta").intValue());
        Assert.assertEquals(1, retVal.get("Bamba").intValue());
        Assert.assertEquals(1, retVal.get("ipsum").intValue());
    }
    @Test
    public void testCalculateWithMergeListWithIgnoreListExampleCaseInsensitive() throws Exception {
        Hashtable<String, List<String>> mergeList = new Hashtable<>();
        ArrayList<String> singleList = new ArrayList<>();
        singleList.add("\\s*L.+sum\\s");
        singleList.add("\\sscripta blandit\\s");
        mergeList.put("Bamba", singleList);

        ArrayList<String> ignoreList = new ArrayList<>();
        ignoreList.add("blandit");

        CruncherImpl cnt = new CruncherImpl(ignoreList, mergeList);
        String text = "Lorem ipsum ad his scripta blandit partiendo. Scripta ad His.";
        Hashtable<String, Integer> retVal;
        cnt.setCaseSensitive(false);
        retVal = cnt.calculate(text);

        Assert.assertEquals(2, retVal.get("his").intValue());
        Assert.assertEquals(2, retVal.get("ad").intValue());
        Assert.assertEquals(1, retVal.get("lorem").intValue());
        Assert.assertNull(retVal.get("Scripta"));
        Assert.assertEquals(2, retVal.get("scripta").intValue());
        Assert.assertEquals(1, retVal.get("partiendo").intValue());
        Assert.assertNull(retVal.get("Bamba"));
    }
}