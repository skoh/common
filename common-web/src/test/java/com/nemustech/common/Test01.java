package com.nemustech.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.nemustech.common.file.Files;
import com.nemustech.sample.model.Files2;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test01 {
	protected Log log = LogFactory.getLog(getClass());
	protected org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());
	protected org.slf4j.Logger logger2 = org.slf4j.LoggerFactory.getLogger(getClass());

	public String targetClass = "1";

	@BeforeClass
	public static void beforeClass() throws Exception {
	}

	@Before
	public void init() throws Exception {
	}

//	@Cacheable(value = "test", key = "#root.caches[0].name + '_' + #root.targetClass + '_' + #root.methodName + '_' + #root.args[0]")
	@Test
	public void test01() throws Exception {
//		Exception e = new Exception("skoh1", new SQLException("skoh2", new RuntimeException("skoh3")));
//		System.out.println(ExceptionUtils.getRootCause(e));
//		log.info("logging");
//		logger.info("log4j");
//		logger2.info("slf4j");
//		String s = "";
//		s = "'Hello World'";
//		s = "'Hello World'.concat('!')";
//		s = "'Hello World'.bytes.length";
//		s = "'Hello World'.getBytes().length";

//		s = "new String('hello world').toUpperCase()";
//		s = "new com.nemustech.common.Test01().targetClass";
//		s = "new com.nemustech.common.Test01().getTargetClass()";

//		s = "T(java.lang.String).valueOf(true)";
//		s = "T(java.lang.Math).random()";
//		s = "T(com.nemustech.common.Test01).targetClass";
//		s = "T(org.apache.commons.lang.builder.ReflectionToStringBuilder).toString('a')";

//		s = "#this";

//		ExpressionParser parser = new SpelExpressionParser();
//		Expression exp = parser.parseExpression(s);
//		Object message = exp.getValue();
//		System.out.println(message);

//		List<Integer> primes = new ArrayList<Integer>();
//		primes.addAll(Arrays.asList(2, 3, 5, 7, 11, 13, 17));
//
//		// create parser and set variable 'primes' as the array of integers
//		ExpressionParser parser = new SpelExpressionParser();
//		StandardEvaluationContext context = new StandardEvaluationContext();
//		context.setVariable("primes", primes);
//
//		// all prime numbers > 10 from the list (using selection ?{...})
//		// evaluates to [11, 13, 17]
//		List<Integer> primesGreaterThanTen = (List<Integer>) parser.parseExpression("#primes.?[#this>10]")
//				.getValue(context);
//		System.out.println(primesGreaterThanTen);

//		Annotation[] annos;
//		annos = this.getClass().getAnnotations();
//		for (Annotation anno : annos) {
//			System.out.println(anno);
//		}

//		Method meth = this.getClass().getMethod("test02");
//		annos = meth.getAnnotations();
//		for (Annotation anno : annos) {
//			System.out.println(anno);
//			if (anno instanceof Cacheable) {
//				Cacheable cache = (Cacheable) anno;
//				System.out.println(cache);
//				System.out.println(Utils.toString(cache.value()));
//				System.out.println(Utils.toString(cache.key()));
//			}
//		}

//		String pw = "0327";
//		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//		String pw1 = passwordEncoder.encode(pw);
//		System.out.println("pw1: " + pw1);
//		System.out.println(passwordEncoder.matches(pw, pw1));

//		System.out.println(new URIBuilder("http://localhost:8050/sample/list.do?id=1")
//				.addParameters((List) Arrays.asList(new BasicNameValuePair("name", "s"))).build());

//		String url = "jdbc:mysql://192.168.1.38:3306/indoornow_dev?useUnicode=yes&characterEncoding=UTF-8&autoReconnect=true";
//		Properties props = new Properties();
//		props.put("user", "indoornow_dev");
//		props.put("password", "indoornow_dev");
//
//		Connection con = DriverManager.getConnection(url, props);
//
//		Statement stat = con.createStatement();
//		String sql = "SELECT * FROM RSSI LIMIT 1";
//		ResultSet rs = stat.executeQuery(sql);
//		rs.next();
//		System.out.println(rs.getString(1));
//
//		String url2 = "jdbc:tibero:thin:@192.168.1.38:8629:s1";
//		Properties props2 = new Properties();
//		props2.put("user", "s1");
//		props2.put("password", "s1");
//
//		Connection con2 = DriverManager.getConnection(url2, props2);
//
//		Statement stat2 = con2.createStatement();
//		String sql2 = "SELECT * FROM T1 WHERE ROWNUM = 1";
//		ResultSet rs2 = stat2.executeQuery(sql2);
//		rs2.next();
//		System.out.println(rs2.getString(1));

//		LogUtil.writeLog("data: " + JsonUtil2.toStringPretty(this));

//		System.out.println(new Date().getTime() + " " + new Date().getTimezoneOffset());

//		System.out.println("mary".matches("john|mary|peter"));

//		long timeMillis = System.currentTimeMillis();
//		System.out.println(timeMillis);
//		System.out.println(new Date(timeMillis));
//		GregorianCalendar CALENDAR = new GregorianCalendar();
//		CALENDAR.setTimeInMillis(timeMillis);
//		CALENDAR.setTimeZone(TimeZone.getTimeZone("GMT")); // UTC
//		System.out.println("" + StringUtil.convertFormat(CALENDAR.get(Calendar.HOUR_OF_DAY), "00")
//				+ StringUtil.convertFormat(CALENDAR.get(Calendar.MINUTE), "00")
//				+ StringUtil.convertFormat(CALENDAR.get(Calendar.SECOND), "00"));

//		Object[] o = new Object[0];
//		System.out.println(o.length);

//		System.out.println("common_class com.nemustech.sample.service.impl.SampleServiceImpl_list_1"
//				.matches("common_class com.nemustech.sample.service.impl.SampleServiceImpl_list.*"));
//		System.out.println(
//				"regex:common_class com.nemustech.sample.service.impl.SampleServiceImpl.*".startsWith("regex:"));

//		System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
//		System.out.println(Arrays.toString(new Sample(), new Sample()));
//		System.out.println(StringUtil.toString(new Sample(), new Sample()));
//		System.out.println(Arrays.asList(new Sample(), new Sample()));
//		System.out.println(StringUtil.toString(Arrays.asList(new Sample(), new Sample())));

//		System.out.println(new Common() instanceof Common);
//		System.out.println(Common.class.isInstance(new Common()));
//
//		System.out.println(new Common().getClass());
//		System.out.println(Common.class);
//		System.out.println(new Common().getClass() == Common.class);
//		System.out.println(new Common().getClass().equals(Common.class));

//		StringBuilder sb = new StringBuilder("abc,");
//		if (sb.toString().endsWith(",")) {
//			sb.deleteCharAt(sb.length() - 1);
//		}
//		System.out.println(sb);

//		System.out.println(new Sample().getClass().getDeclaredField("reg_id"));
//		System.out.println(FieldUtils.getDeclaredField(new Sample().getClass(), "name"));
//		System.out.println(FieldUtils.getDeclaredField(new Sample().getClass(), "name", true));
//		System.out.println(FieldUtils.getField(new Sample().getClass(), "reg_id1", true));
//		Sample sample = new Sample();
//		sample.setReg_id("reg_id");
//		System.out.println(ReflectionUtil.getValue(sample, "reg_id1"));String messageFoamat = "{\"Message\":\"{0}\"}";

//		System.out.println(StringUtil.getMessage("\"Message\":\"{0}\"", "Success"));

//		Child c = new Child();
//		Type type = c.getClass().getGenericSuperclass();
//		System.out.println("class: " + type.getClass());
//		if (type instanceof ParameterizedType) {
//			ParameterizedType pt = (ParameterizedType) type;
//			System.out.println("object: " + ((Class) pt.getActualTypeArguments()[0]).newInstance());
//		}

//		Object obj = "1";
//		System.out.println(obj.toString());

//		Files f = new Files();
//		setBind(f).setReg_id("reg_id2");
//		;
//		System.out.println(f);
	}

	Files setBind(Files f) {
		f.setReg_id("reg_id");
		return f;
	}

	class Parent<F extends Files> {
	}

	class Parent2<F extends Files> extends Parent<F> {
	}

	class Child extends Parent2<Files2> {
//		Child() throws Exception {
//			Type type = this.getClass().getGenericSuperclass();
//			System.out.println("class: " + type.getClass());
//			if (type instanceof ParameterizedType) {
//				ParameterizedType pt = (ParameterizedType) type;
//				System.out.println("object: " + ((Class) pt.getActualTypeArguments()[0]).newInstance());
//			}
//		}
	}

	class Test2 {
	}

	class Test3 extends Test2 {
	}

	public String getTargetClass() throws Exception {
		return targetClass;
	}

	public static void main(String[] args) throws Exception {
//		Test01 test01 = new Test01();
//		test01.test01();
//		System.out.println(StringUtil.toString(test01));
//		System.out.println(BeanUtils.describe(test01));

//		Pattern pattern = Pattern.compile("^(?!state|government|head).*$");
//		Pattern pattern = Pattern.compile("^$(?!state)");
//		String s = "state";
//		Matcher matcher = pattern.matcher(s);
//		boolean bl = matcher.find();
//		System.out.println(bl);
//
//		s = "state1";
//		matcher = pattern.matcher(s);
//		bl = matcher.find();
//		System.out.println(bl);
//
//		s = "1state";
//		matcher = pattern.matcher(s);
//		bl = matcher.find();
//		System.out.println(bl);
//
//		s = "government";
//		matcher = pattern.matcher(s);
//		bl = matcher.find();
//		System.out.println(bl);
//
//		s = "head";
//		matcher = pattern.matcher(s);
//		bl = matcher.find();
//		System.out.println(bl);
//
//		s = "Abc";
//		matcher = pattern.matcher(s);
//		bl = matcher.find();
//		System.out.println(bl);

//		String regular = "(?!svn$|cvs$).*";
//		System.out.println("svn".matches(regular));
//		System.out.println("svn1".matches(regular));
//		System.out.println("1svn".matches(regular));
//		System.out.println("cvs".matches(regular));
//		System.out.println("1cvs".matches(regular));
//		System.out.println("cvs1".matches(regular));
//		System.out.println("abc".matches(regular));

//		String regular = "(?!null)";
//		System.out.println("null".matches(regular));
//		System.out.println("".matches(regular));
//		System.out.println("null1".matches(regular));
//		System.out.println(" ".matches(regular));
	}
}
