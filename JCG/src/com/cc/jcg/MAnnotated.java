package com.cc.jcg;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

abstract class MAnnotated<T>
	implements MHasImports {

    protected final LinkedList<Annotation> annotations = new LinkedList<Annotation>();
    protected final LinkedHashMap<MAnnotation, Object[]> mAnnotations = new LinkedHashMap<MAnnotation, Object[]>();
    protected final LinkedList<String> preDefinitionLines = new LinkedList<String>();
    protected final AtomicBoolean generatedAdded = new AtomicBoolean(false);
    private final Map<String, Object> extraData = new ConcurrentHashMap<String, Object>();

    public final Map<String, Object> getExtraData() {
	return extraData;
    }

    protected MAnnotated() {
	super();
	if (!MBundle.EXCLUDE_GENERATED_ANNOTATION.get()) {
	    addGeneratedAnnotation();
	}
    }

    public final List<Annotation> getAnnotations() {
	return Collections.unmodifiableList(annotations);
    }

    public final T addPreDefinitionLine(String line) {
	preDefinitionLines.add(line);
	return (T) this;
    }

    public final T addPreDefinitionLine(String line, Class<?>... imports) {
	addPreDefinitionLine(line);
	for (Class<?> imp : imports) {
	    addImport(imp);
	}
	return (T) this;
    }

    public final T addPreDefinitionLine(String line, MTypeRef<?>... imports) {
	addPreDefinitionLine(line);
	for (MTypeRef imp : imports) {
	    addImport(imp);
	}
	return (T) this;
    }

    public final T addPreDefinitionLine(String line, MType... imports) {
	addPreDefinitionLine(line);
	for (MType imp : imports) {
	    addImport(imp);
	}
	return (T) this;
    }

    public final T removeAllAnnotations() {
	annotations.clear();
	return (T) this;
    }

    public final T addAnnotation(Annotation annotation) {
	annotations.add(annotation);
	return (T) this;
    }

    public final T addAnnotations(Annotation... annotations) {
	addAnnotations(Arrays.asList(annotations));
	return (T) this;
    }

    public final T addAnnotations(Collection<Annotation> annotations) {
	for (final Annotation annotation : annotations) {
	    addAnnotation(annotation);
	}
	return (T) this;
    }

    public T annotate(MAnnotation annotation) {
	return annotate(annotation, new Object[] {});
    }

    public T annotate(MAnnotation annotation, Object[] values) {
	if (isValidAnnotation(annotation)) {
	    mAnnotations.put(annotation, values);
	} else {
	    throw new RuntimeException("Annotation type is ElementType." + annotation.getType().name());
	}
	return (T) this;
    }

    protected boolean isValidAnnotation(MAnnotation annotation) {
	return true;// TODO: override!
    }

    private void addGeneratedAnnotation() {
	if (this instanceof MType && !(this instanceof MInnerType)) {
	    if (!((MType) this).isTemplate()) {
		if (!generatedAdded.getAndSet(true)) {
		    annotations.add(new MGenerated() {

			@Override
			public Class<? extends Annotation> annotationType() {
			    return MGenerated.class;
			}
		    });
		}
	    }
	}
    }

    public abstract String getPackageName();

    protected final void addImport(TreeSet<String> imports, Class<?> type) {
	MFunctions.addImport(imports, type, getPackageName());
    }

    protected final void addImport(TreeSet<String> imports, MTypeRef type) {
	MFunctions.addImport(imports, type, getPackageName());
    }

    protected final void addImport(TreeSet<String> imports, String fullname) {
	MFunctions.addImport(imports, fullname);
    }

    protected final void addImport(TreeSet<String> imports, Collection<String> fullnames) {
	for (final String fullname : fullnames) {
	    MFunctions.addImport(imports, fullname);
	}
    }

    @Override
    public TreeSet<String> getImports() {
	final TreeSet<String> imports = new TreeSet<String>();
	for (final MAnnotation annotation : mAnnotations.keySet()) {
	    imports.addAll(annotation.getImports());
	}
	for (final Annotation annotation : annotations) {
	    addImportsRecursively(imports, annotation);
	}
	return imports;
    }

    protected void addImportsRecursively(TreeSet<String> imports, Annotation annotation) {
	addImport(imports, annotation.annotationType());
	final Class<? extends Annotation> annotationType = annotation.getClass();
	for (final Method m : annotationType.getDeclaredMethods()) {
	    try {
		m.setAccessible(true);
		final Object av = m.invoke(annotation);
		if (av != null) {
		    final Class<?> returnType = m.getReturnType();
		    if (returnType.isArray()) {
			final Object[] array = (Object[]) av;
			if (array.length > 0) {
			    for (final Object o : array) {
				if (o instanceof Annotation) {
				    addImportsRecursively(imports, (Annotation) o);
				}
			    }
			}
		    } else if (av instanceof Annotation) {
			addImportsRecursively(imports, (Annotation) av);
		    } else if (av instanceof Enum) {
			imports.add(av.getClass().getName());
		    }
		}
	    } catch (final Exception e) {
		// continue...
	    }
	}
    }

    protected void appendAnnotation(StringBuffer sb) {
	final StringBuffer asb = new StringBuffer();
	for (final String line : preDefinitionLines) {
	    asb.append(line);
	}
	for (final MAnnotation annotation : mAnnotations.keySet()) {
	    appendAnnotation(asb, annotation);
	}
	for (final Annotation annotation : annotations) {
	    appendAnnotation(asb, annotation, true);
	}
	sb.append(asb.toString().replace("()", ""));
    }

    private void appendAnnotation(StringBuffer asb, MAnnotation annotation) {
	asb.append("@" + annotation.getName());
	final Object[] values = mAnnotations.get(annotation);
	List<MAbstractMethod> methods = new ArrayList<MAbstractMethod>(annotation.getMethods());
	if (!methods.isEmpty()) {
	    Collections.sort(methods, new Comparator<MAbstractMethod>() {

		@Override
		public int compare(MAbstractMethod o1, MAbstractMethod o2) {
		    return o1.getName().compareTo(o2.getName());
		}
	    });
	    asb.append("(");
	    int indx = 0;
	    for (final MAbstractMethod m : methods) {
		final Object av = values[indx];
		final MTypeRef returnType = m.getReturnType();
		if (returnType instanceof MTypeRefJava) {
		    annotationValue(asb, m.getName(), ((MTypeRefJava) returnType).getRef(), av);
		} else if (returnType instanceof MTypeRefModel) {
		    throw new RuntimeException("Unsupported type for MAnnotation method " + m.getName() + ": " + returnType);
		} else if (returnType instanceof MTypeRefGeneric) {
		    throw new RuntimeException("Unsupported type for MAnnotation method " + m.getName() + ": " + returnType);
		}
		indx = indx + 1;
	    }
	    if (asb.toString().endsWith(", ")) {
		asb.delete(asb.length() - 2, asb.length());
	    }
	    asb.append(")");
	}
	asb.append("\n");
    }

    private void appendAnnotation(StringBuffer asb, Annotation annotation, boolean cr) {
	asb.append("@" + annotation.annotationType().getSimpleName());
	asb.append("(");
	final Class<? extends Annotation> annotationType = annotation.getClass();
	List<Method> methods = new ArrayList<Method>(Arrays.asList(annotationType.getDeclaredMethods()));
	Collections.sort(methods, new Comparator<Method>() {

	    @Override
	    public int compare(Method o1, Method o2) {
		return o1.getName().compareTo(o2.getName());
	    }
	});
	for (final Method m : methods) {
	    try {
		m.setAccessible(true);
		final Object av = m.invoke(annotation);
		boolean isDefaultValue = false;
		try {
		    final Object fv = annotationType.getInterfaces()[0].getDeclaredMethod(m.getName()).getDefaultValue();
		    isDefaultValue = fv != null && fv.equals(av);
		    isDefaultValue = isDefaultValue || fv != null && fv.equals("") && av == null;
		    isDefaultValue = isDefaultValue || fv == null && av == null;
		} catch (final Exception e) {
		    // OK
		}
		if (!isDefaultValue && av != null && !m.getName().equals("annotationType")) {
		    annotationValue(asb, m.getName(), m.getReturnType(), av);
		}
	    } catch (final IllegalArgumentException e) {
		e.printStackTrace();
	    } catch (final IllegalAccessException e) {
		e.printStackTrace();
	    } catch (final InvocationTargetException e) {
		e.printStackTrace();
	    }
	}
	if (asb.toString().endsWith(", ")) {
	    asb.delete(asb.length() - 2, asb.length());
	}
	asb.append(")");
	if (cr && !(this instanceof MParameter)) {
	    asb.append("\n");
	}
    }

    private void annotationValue(StringBuffer asb, final String methodName, Class<?> returnType, final Object av) {
	if (methodName.equals("value") && String.class.isAssignableFrom(returnType)) {
	    asb.append(methodName + " = \"" + av + "\"");
	    asb.append(", ");
	} else if (methodName.equals("value") && String[].class.isAssignableFrom(returnType)) {
	    final String[] ss = (String[]) av;
	    if (ss.length == 0) {
		// @FLAGS({})
		asb.append("{}");
	    } else if (ss.length == 1) {
		// @FLAGS("NOT_GROUPABLE")
		asb.append("\"" + ss[0] + "\"");
	    } else {
		// @FLAGS({ "NOT_GROUPABLE", "NOT_GROUPABLE" })
		StringBuffer sb = new StringBuffer();
		for (String s : ss) {
		    sb.append("\"" + s + "\"" + ", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		asb.append("{ " + sb.toString() + " }");
	    }
	    asb.append(", ");
	} else if (String[].class.isAssignableFrom(returnType)) {
	    final String[] ss = (String[]) av;
	    if (ss.length == 0) {
		// @FLAGS(some = {})
		asb.append(methodName + " = {}");
	    } else if (ss.length == 1) {
		// @FLAGS(some = "NOT_GROUPABLE")
		asb.append(methodName + " = \"" + ss[0] + "\"");
		asb.append(", ");
	    } else {
		// @FLAGS(some = { "NOT_GROUPABLE", "NOT_GROUPABLE" })
		StringBuffer sb = new StringBuffer();
		for (String s : ss) {
		    sb.append("\"" + s + "\"" + ", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		asb.append(methodName + " = { " + sb.toString() + " }");
	    }
	} else if (String.class.isAssignableFrom(returnType)) {
	    asb.append(methodName + " = \"" + av.toString() + "\"");
	    asb.append(", ");
	} else if (boolean.class.isAssignableFrom(returnType)) {
	    final boolean s = (Boolean) av;
	    asb.append(methodName + (s ? "= true" : "= false"));
	    asb.append(", ");
	} else if (int.class.isAssignableFrom(returnType)) {
	    asb.append(methodName + " = " + av);
	    asb.append(", ");
	} else if (Class.class.isAssignableFrom(returnType)) {
	    final Class c = (Class) av;
	    asb.append(methodName + " = " + c.getSimpleName() + ".class");
	    asb.append(", ");
	} else if (returnType.isArray()) {
	    final Object[] array = (Object[]) av;
	    if (array.length > 0) {
		asb.append("{");
		for (final Object o : array) {
		    final StringBuffer asb2 = new StringBuffer();
		    if (o instanceof Annotation) {
			appendAnnotation(asb2, (Annotation) o, false);
		    } else {
			asb2.append(o.toString());// !?
		    }
		    if (array[array.length - 1] != o) {
			asb2.append(",");
		    }
		    asb.append("\n");
		    asb.append("\t" + asb2.toString().replace("\n", ""));
		}
		asb.append("\n}");
		asb.append(", ");
	    }
	} else if (av instanceof Annotation) {
	    asb.append(methodName + " = ");
	    final StringBuffer asb2 = new StringBuffer();
	    appendAnnotation(asb2, (Annotation) av, false);
	    asb.append(asb2.toString().replace("\n", ""));
	    asb.append(", ");
	} else if (av instanceof Enum) {
	    asb.append(methodName + " = " + av.getClass().getSimpleName() + "." + ((Enum) av).name());
	    asb.append(", ");
	} else {
	    asb.append(methodName + " = " + av.toString());
	    asb.append(", ");
	}
    }
}
