/*
 * The MIT License
 *
 * Copyright 2015 Antonio Rabelo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package br.tennaito.rsql.jpa;


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import org.junit.Before;
import org.junit.Test;

import br.tennaito.rsql.builder.BuilderTools;
import br.tennaito.rsql.jpa.entity.Course;
import br.tennaito.rsql.misc.SimpleMapper;
import br.tennaito.rsql.parser.ast.ComparisonOperatorProxy;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;

/**
 * @author AntonioRabelo
 */
public class JpaVisitorTest extends AbstractVisitorTest<Course> {

    @Before
    public void setUp() throws Exception {
    	entityManager = EntityManagerFactoryInitializer.getEntityManagerFactory().createEntityManager();
        entityClass = Course.class;
    }

    @Test
    public void testSimpleSelection() throws Exception {
    	Node rootNode = new RSQLParser().parse("id==1");
    	RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
    	CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

    	List<Course> courses = entityManager.createQuery(query).getResultList();
    	assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testNotEqualSelection() throws Exception {
    	Node rootNode = new RSQLParser().parse("id!=1");
    	RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
    	CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

    	List<Course> courses = entityManager.createQuery(query).getResultList();
    	assertEquals(0, courses.size());
    }

    @Test
    public void testGreaterThanSelection() throws Exception {
    	Node rootNode = new RSQLParser().parse("id=gt=1");
    	RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
    	CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

    	List<Course> courses = entityManager.createQuery(query).getResultList();
    	assertEquals(0, courses.size());
    }

    @Test
    public void testGreaterThanEqualSelection() throws Exception {
    	Node rootNode = new RSQLParser().parse("id=ge=1");
    	RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
    	CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

    	List<Course> courses = entityManager.createQuery(query).getResultList();
    	assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testLessThanSelection() throws Exception {
    	Node rootNode = new RSQLParser().parse("id=lt=1");
    	RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
    	CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

    	List<Course> courses = entityManager.createQuery(query).getResultList();
    	assertEquals(0, courses.size());
    }

    @Test
    public void testLessThanEqualSelection() throws Exception {
    	Node rootNode = new RSQLParser().parse("id=le=1");
    	RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
    	CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

    	List<Course> courses = entityManager.createQuery(query).getResultList();
    	assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testInSelection() throws Exception {
    	Node rootNode = new RSQLParser().parse("id=in=(1,2,3,4)");
    	RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
    	CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

    	List<Course> courses = entityManager.createQuery(query).getResultList();
    	assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testOutSelection() throws Exception {
    	Node rootNode = new RSQLParser().parse("id=out=(1,2,3,4)");
    	RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
    	CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

    	List<Course> courses = entityManager.createQuery(query).getResultList();
    	assertEquals(0, courses.size());
    }

    @Test
    public void testLikeSelection() throws Exception {
    	Node rootNode = new RSQLParser().parse("name==*Course");
    	RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
    	CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

    	List<Course> courses = entityManager.createQuery(query).getResultList();
    	assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testNotLikeSelection() throws Exception {
    	Node rootNode = new RSQLParser().parse("name!=*Course");
    	RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
    	CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

    	List<Course> courses = entityManager.createQuery(query).getResultList();
    	assertEquals(0, courses.size());
    }


    @Test
    public void testIsNullSelection() throws Exception {
    	Node rootNode = new RSQLParser().parse("name==null");
    	RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
    	CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

    	List<Course> courses = entityManager.createQuery(query).getResultList();
    	assertEquals(0, courses.size());
    }

    @Test
    public void testNotIsNullSelection() throws Exception {
    	Node rootNode = new RSQLParser().parse("name!=null");
    	RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
    	CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

    	List<Course> courses = entityManager.createQuery(query).getResultList();
    	assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testUndefinedComparisonOperator() {
    	try {
    		ComparisonOperator newOp = new ComparisonOperator("=def=");
    		Set<ComparisonOperator> set = new HashSet<ComparisonOperator>();
    		set.add(newOp);
	    	Node rootNode = new RSQLParser(set).parse("id=def=null");
	    	RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
	    	CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);
    		List<Course> courses = entityManager.createQuery(query).getResultList();
    		fail();
    	} catch(Exception e) {
    		assertEquals("Unknown operator: =def=", e.getMessage());
    	}
    }

    @Test
    public void testDefinedComparisonOperator() {
    	// define the new operator
		ComparisonOperator newOp = new ComparisonOperator("=def=");
		Set<ComparisonOperator> set = new HashSet<ComparisonOperator>();
		set.add(newOp);
		// execute parser
    	Node rootNode = new RSQLParser(set).parse("id=def=1");

    	JpaCriteriaQueryVisitor<Course> visitor = new JpaCriteriaQueryVisitor<Course>();
    	// define new operator resolver
    	PredicateBuilderStrategy predicateStrategy = new PredicateBuilderStrategy() {
			@Override
			public <T> Predicate createPredicate(Node node, Class<T> entity,
					EntityManager manager, BuilderTools tools)
					throws IllegalArgumentException {
				ComparisonNode comp = ((ComparisonNode)node);
				ComparisonNode def = new ComparisonNode(ComparisonOperatorProxy.EQUAL.getOperator(), comp.getSelector(), comp.getArguments());
				return PredicateBuilder.createPredicate(def, entity, manager, tools);
			}
		};
    	visitor.getBuilderTools().setPredicateBuilder(predicateStrategy);

    	CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);
		List<Course> courses = entityManager.createQuery(query).getResultList();
		assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testAssociationSelection() throws Exception {
    	Node rootNode = new RSQLParser().parse("department.id==1");
    	RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
    	CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

    	List<Course> courses = entityManager.createQuery(query).getResultList();
    	assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testAssociationAliasSelection() throws Exception {
    	Node rootNode = new RSQLParser().parse("dept.id==1");
    	JpaCriteriaQueryVisitor<Course> visitor = new JpaCriteriaQueryVisitor<Course>();
    	// create alias
    	Map<String, String> map = new HashMap<String, String>();
    	map.put("dept", "department");
    	// add to SimpleMapper
    	((SimpleMapper)visitor.getBuilderTools().getPropertiesMapper()).addMapping(Course.class, map);

    	CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);
    	List<Course> courses = entityManager.createQuery(query).getResultList();
    	assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testAndSelection() throws Exception {
    	Node rootNode = new RSQLParser().parse("department.id==1;id==2");
    	RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
    	CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

    	List<Course> courses = entityManager.createQuery(query).getResultList();
    	assertEquals(0, courses.size());
    }

    @Test
    public void testOrSelection() throws Exception {
    	Node rootNode = new RSQLParser().parse("department.id==1,id==2");
    	RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
    	CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

    	List<Course> courses = entityManager.createQuery(query).getResultList();
    	assertEquals("Testing Course", courses.get(0).getName());
    }
}