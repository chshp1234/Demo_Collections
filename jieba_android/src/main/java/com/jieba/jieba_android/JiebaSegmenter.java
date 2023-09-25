package com.jieba.jieba_android;

import static com.jieba.jieba_android.JiebaSegmenter.SegMode.SEARCH;
import static com.jieba.jieba_android.Utility.LOGTAG;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.jieba.jieba_android.viterbi.FinalSeg;


public class JiebaSegmenter {
    private static volatile boolean initReady = false;
    private static volatile boolean isSatrt = false;
    private static final int SLEEP_TIME = 100;

    private static final long MAX_TIME = 5 * 60 * 1000L;

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(1, 1,
            MAX_TIME, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    static {
        EXECUTOR.allowCoreThreadTimeOut(true);
    }

    /**
     * 可以指定分词模式是索引模式（INDEX）或搜索引擎模式（SEARCH），两者的差别在于搜索引擎模式分词更精细，索引模式相对更粗粒度
     */
    public enum SegMode {
        INDEX,
        SEARCH
    }

    public static void init(final Context context) {
        if (isSatrt) {
            return;
        }
        isSatrt = true;

        EXECUTOR.execute(() -> {
            // 初始化结巴分词,不会阻塞初始化进度，不管有没有完成，都继续，在调用的时候判断是否完成初始化，没完成则等待完成
            synchronized (JiebaSegmenter.class) {
                long start = System.currentTimeMillis();
                AssetManager assetManager = context.getAssets();
                FinalSeg.getInstance().loadModel(assetManager);
                WordDictionary.getInstance().loadData(assetManager);
                initReady = true;
                long end = System.currentTimeMillis();
                Log.d(LOGTAG, String.format("init complete takes:%d ms", end - start));
                Log.d(LOGTAG, "jieba init finished.");
                JiebaSegmenter.class.notifyAll();
            }
        });
    }

    public static void release() {
        if (!isSatrt) {
            return;
        }

        isSatrt = false;
        initReady = false;

        EXECUTOR.execute(() -> {

            Log.i("EXECUTOR", "terminated");

            long start = System.currentTimeMillis();
            FinalSeg.release();
            WordDictionary.release();
//                System.gc();
            //快速回收占用大的/无效的内存
            Runtime.getRuntime().gc();
            long end = System.currentTimeMillis();
            Log.d(LOGTAG, String.format("release complete :%d ms", end - start));

        });
    }

    private JiebaSegmenter() {
    }

    public static void getDividedStringAsync(final String query, final RequestCallback<ArrayList<String>> callback) {
        getDividedStringAsync(query, SEARCH, callback);
    }

    public static void getDividedStringAsync(final String query, final SegMode mode, final RequestCallback<ArrayList<String>> callback) {
        if (!isSatrt) {
            callback.onError("should init first");
        }

        Runnable runnable = () -> {
//            if (!initReady) {
//                synchronized (JiebaSegmenter.class) {
//                    while (!initReady) {
//                        try {
//                            JiebaSegmenter.class.wait(5000L);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }

            if (!initReady) {
                callback.onError("not init");
                return;
            }

            final ArrayList<String> dividedStrs = getDividedString(query, mode);
            callback.onSuccess(dividedStrs);
        };

        EXECUTOR.execute(runnable);
    }


    private static ArrayList<String> getDividedString(String query, SegMode mode) {
        if (!initReady) {
            return null;
        }

        long start = System.currentTimeMillis();

        List<SegToken> lst = process(query, mode);

        ArrayList<String> resultLst = new ArrayList<>();

        for (SegToken st : lst) {
            resultLst.add(st.word);
        }

        long end = System.currentTimeMillis();
        Log.d(LOGTAG, String.format("getDivideList takes %d ms", end - start));

        return resultLst;
    }

    private static List<SegToken> process(String query, SegMode mode) {
        if (!initReady) {
            return null;
        }

        List<SegToken> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int offset = 0;
        for (int i = 0; i < query.length(); ++i) {
            char ch = CharacterUtil.regularize(query.charAt(i));
            if (CharacterUtil.ccFind(ch))
                sb.append(ch);
            else {
                if (sb.length() > 0) {
                    // process
                    if (mode == SEARCH) {
                        for (String word : sentenceProcess(sb.toString())) {
                            tokens.add(new SegToken(word, offset, offset += word.length()));
                        }
                    } else {
                        for (String token : sentenceProcess(sb.toString())) {
                            if (token.length() > 2) {
                                String gram2;
                                int j = 0;
                                for (; j < token.length() - 1; ++j) {
                                    gram2 = token.substring(j, j + 2);
                                    if (WordDictionary.getInstance().containsWord(gram2))
                                        tokens.add(new SegToken(gram2, offset + j, offset + j + 2));
                                }
                            }
                            if (token.length() > 3) {
                                String gram3;
                                int j = 0;
                                for (; j < token.length() - 2; ++j) {
                                    gram3 = token.substring(j, j + 3);
                                    if (WordDictionary.getInstance().containsWord(gram3))
                                        tokens.add(new SegToken(gram3, offset + j, offset + j + 3));
                                }
                            }
                            tokens.add(new SegToken(token, offset, offset += token.length()));
                        }
                    }
                    sb = new StringBuilder();
                    offset = i;
                }
                if (WordDictionary.getInstance().containsWord(query.substring(i, i + 1)))
                    tokens.add(new SegToken(query.substring(i, i + 1), offset, ++offset));
                else
                    tokens.add(new SegToken(query.substring(i, i + 1), offset, ++offset));
            }
        }

        if (sb.length() > 0)
            if (mode == SEARCH) {
                for (String token : sentenceProcess(sb.toString())) {
                    tokens.add(new SegToken(token, offset, offset += token.length()));
                }
            } else {
                for (String token : sentenceProcess(sb.toString())) {
                    if (token.length() > 2) {
                        String gram2;
                        int j = 0;
                        for (; j < token.length() - 1; ++j) {
                            gram2 = token.substring(j, j + 2);
                            if (WordDictionary.getInstance().containsWord(gram2))
                                tokens.add(new SegToken(gram2, offset + j, offset + j + 2));
                        }
                    }
                    if (token.length() > 3) {
                        String gram3;
                        int j = 0;
                        for (; j < token.length() - 2; ++j) {
                            gram3 = token.substring(j, j + 3);
                            if (WordDictionary.getInstance().containsWord(gram3))
                                tokens.add(new SegToken(gram3, offset + j, offset + j + 3));
                        }
                    }
                    tokens.add(new SegToken(token, offset, offset += token.length()));
                }
            }

        return tokens;
    }

    private static Map<Integer, List<Integer>> createDAG(String sentence) {
        Map<Integer, List<Integer>> dag = new HashMap<>();
        Element trie = WordDictionary.getInstance().getTrie();
        char[] chars = sentence.toCharArray();
        int N = chars.length;
        int i = 0, j = 0;
        while (i < N) {
            Hit hit = trie.match(chars, i, j - i + 1);
            if (hit.isPrefix() || hit.isMatch()) {
                if (hit.isMatch()) {
                    if (!dag.containsKey(i)) {
                        List<Integer> value = new ArrayList<>();
                        dag.put(i, value);
                        value.add(j);
                    } else
                        dag.get(i).add(j);
                }
                j += 1;
                if (j >= N) {
                    i += 1;
                    j = i;
                }
            } else {
                i += 1;
                j = i;
            }
        }
        for (i = 0; i < N; ++i) {
            if (!dag.containsKey(i)) {
                List<Integer> value = new ArrayList<>();
                value.add(i);
                dag.put(i, value);
            }
        }
        return dag;
    }

    /**
     * 计算有向无环图的一条最大路径，从后向前，利用贪心算法，每一步只需要找出到达该字符的最大概率字符作为所选择的路径
     *
     * @param sentence
     * @param dag
     * @return
     */
    private static Map<Integer, Pair<Integer>> calc(String sentence, Map<Integer, List<Integer>> dag) {
        int N = sentence.length();
        HashMap<Integer, Pair<Integer>> route = new HashMap<>();
        route.put(N, new Pair<>(0, 0.0));
        for (int i = N - 1; i > -1; i--) {
            Pair<Integer> candidate = null;
            for (Integer x : dag.get(i)) {
                double freq = WordDictionary.getInstance().getFreq(sentence.substring(i, x + 1)) + route.get(x + 1).freq;
                if (null == candidate) {
                    candidate = new Pair<>(x, freq);
                } else if (candidate.freq < freq) {
                    candidate.freq = freq;
                    candidate.key = x;
                }
            }
            route.put(i, candidate);
        }
        return route;
    }

    private static List<String> sentenceProcess(String sentence) {
        List<String> tokens = new ArrayList<>();
        int N = sentence.length();

        long start = System.currentTimeMillis();
        // 将一段文字转换成有向无环图，该有向无环图包含了跟字典文件得出的所有可能的单词切分
        Map<Integer, List<Integer>> dag = createDAG(sentence);

        Map<Integer, Pair<Integer>> route = calc(sentence, dag);

        int x = 0;
        int y = 0;
        String buf;
        StringBuilder sb = new StringBuilder();
        while (x < N) { // 遍历一遍贪心算法生成的最小路径分词结果，对单蹦个的字符看看能不能粘合成一个词汇
            y = route.get(x).key + 1;
            String lWord = sentence.substring(x, y);
            if (y - x == 1)
                sb.append(lWord);
            else {
                if (sb.length() > 0) {
                    buf = sb.toString();
                    sb = new StringBuilder();
                    if (buf.length() == 1) { // 如果两个单词之间只有一个单蹦个的字符，添加
                        tokens.add(buf);
                    } else {
                        if (WordDictionary.getInstance().containsWord(buf)) { // 如果连续单蹦个的字符粘合成的一个单词在字典树里，作为一个单词添加
                            tokens.add(buf);
                        } else {
                            FinalSeg.getInstance().cut(buf, tokens); // 如果连续单蹦个的字符粘合成的一个单词不在字典树里，使用维特比算法计算每个字符BMES如何选择使得概率最大
                        }
                    }
                }
                tokens.add(lWord);
            }
            x = y;
        }
        buf = sb.toString();
        if (buf.length() > 0) { // 处理余下的部分
            if (buf.length() == 1) {
                tokens.add(buf);
            } else {
                if (WordDictionary.getInstance().containsWord(buf)) {
                    tokens.add(buf);
                } else {
                    FinalSeg.getInstance().cut(buf, tokens);
                }
            }

        }
        return tokens;
    }

    private static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {

        }
    }
}
