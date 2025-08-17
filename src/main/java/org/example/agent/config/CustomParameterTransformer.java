package org.example.agent.config;

import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.logging.QueryLogEntryCreator;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class CustomParameterTransformer implements QueryLogEntryCreator {

    private static final String TS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final SimpleDateFormat TS_FMT = new SimpleDateFormat(TS_PATTERN);

    @Override
    public String getLogEntry(ExecutionInfo execInfo,
                              List<QueryInfo> queryInfoList,
                              boolean writeDataSourceName,
                              boolean writeConnectionId,
                              boolean writeIsolation) {
        // datasource-proxy는 Statement 배치/Prepared 배치에 따라
        // - Statement 배치: QueryInfo N개
        // - Prepared/Callable 배치: QueryInfo 1개 + parametersList N세트
        // 를 준다. 아래는 두 경우 모두 처리.
        List<String> rendered = new ArrayList<>();

        for (QueryInfo qi : queryInfoList) {
            final String raw = qi.getQuery();
            List<List<ParameterSetOperation>> paramsList = qi.getParametersList(); // N개의 파라미터 세트(배치 지원)

            if (paramsList == null || paramsList.isEmpty()) {
                rendered.add(raw); // 파라미터 없는 경우
                continue;
            }

            for (List<ParameterSetOperation> ops : paramsList) {
                // index -> valueString 맵 구성
                Map<Integer, String> idxToVal = new HashMap<>();
                for (ParameterSetOperation op : ops) {
                    Object[] args = op.getArgs();
                    if (args == null || args.length == 0) continue;

                    // 대부분의 setX(int index, value) 시그니처
                    // setNull(int index, int sqlType) 도 처리
                    Integer index = toInt(args[0]);
                    String valStr;

                    if (isSetNull(op)) {
                        valStr = "NULL";
                    } else if (args.length >= 2) {
                        valStr = renderValue(args[1]);
                    } else {
                        // 예외적 케이스 방어
                        valStr = "NULL";
                    }
                    if (index != null) {
                        idxToVal.put(index, valStr);
                    }
                }

                // 인덱스 오름차순으로 '?' 치환
                String inlined = inlineByIndex(raw, idxToVal);
                rendered.add(inlined);
            }
        }

        // 여러 개면 줄바꿈으로 이어붙임
        String body = rendered.stream().collect(Collectors.joining("\n"));

        return body.trim().replaceAll("\\n", "").replaceAll("\\s+", " ");
    }

    private static boolean isSetNull(ParameterSetOperation op) {
        return op != null && "setNull".equals(op.getMethod().getName());
    }

    private static Integer toInt(Object o) {
        if (o == null) return null;
        if (o instanceof Integer) return (Integer) o;
        if (o instanceof Number) return ((Number) o).intValue();
        try {
            return Integer.parseInt(String.valueOf(o));
        } catch (Exception e) {
            return null;
        }
    }

    // '?'를 인덱스 순서대로 안전 치환
    private static String inlineByIndex(String sql, Map<Integer, String> idxToVal) {
        String result = sql;
        List<Integer> keys = new ArrayList<>(idxToVal.keySet());
        Collections.sort(keys);
        for (Integer idx : keys) {
            result = result.replaceFirst("\\?", Matcher.quoteReplacement(idxToVal.get(idx)));
        }
        return result;
    }

    // 값 렌더링: 숫자/불리언은 그대로, 문자열/날짜는 quote, 바이트류는 길이 표시
    private static String renderValue(Object v) {
        if (v == null) return "NULL";
        if (v instanceof Number || v instanceof Boolean) {
            return String.valueOf(v);
        }
        if (v instanceof Date) {
            // java.sql.Date/Time/Timestamp 포함
            Timestamp ts = (v instanceof Timestamp) ? (Timestamp) v : new Timestamp(((Date) v).getTime());
            return "'" + TS_FMT.format(ts) + "'";
        }
        if (v instanceof byte[]) {
            return "<bytes:" + ((byte[]) v).length + ">";
        }
        String s = String.valueOf(v);
        // 작은따옴표 escape
        s = s.replace("'", "''");
        return "'" + s + "'";
    }


    // java.util.regex.Matcher import
    private static final class Matcher {
        private static final java.util.regex.Pattern BACKSLASH = java.util.regex.Pattern.compile("\\\\");
        private static final java.util.regex.Pattern DOLLAR = java.util.regex.Pattern.compile("\\$");

        // Log4j2/SLF4J replaceFirst용 안전 치환
        static String quoteReplacement(String s) {
            if (s == null) return "";
            s = BACKSLASH.matcher(s).replaceAll("\\\\\\\\");
            s = DOLLAR.matcher(s).replaceAll("\\\\\\$");
            return s;
        }
    }
}
