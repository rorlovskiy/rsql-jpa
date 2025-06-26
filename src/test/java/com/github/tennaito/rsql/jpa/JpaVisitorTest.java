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
package com.github.tennaito.rsql.jpa;


import com.github.tennaito.rsql.builder.BuilderTools;
import com.github.tennaito.rsql.jpa.entity.Course;
import com.github.tennaito.rsql.misc.SimpleMapper;
import com.github.tennaito.rsql.parser.ast.ComparisonOperatorProxy;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.AbstractNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;

/**
 * @author AntonioRabelo
 */
public class JpaVisitorTest extends AbstractVisitorTest<Course> {

    @Before
    public void setUp() {
        entityManager = EntityManagerFactoryInitializer.getEntityManagerFactory().createEntityManager();
        entityClass = Course.class;
    }

    @Test
    public void testUnknowProperty() {
        try {
            Node rootNode = new RSQLParser().parse("invalid==1");
            RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
            CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

            List<Course> courses = entityManager.createQuery(query).getResultList();
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Unknown property: invalid from entity " + Course.class.getName(), e.getMessage());
        }
    }

    @Test
    public void testSimpleSelection() {
        Node rootNode = new RSQLParser().parse("id==1");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testSimpleSelectionWhenPassingArgumentInTemplate() {
        Node rootNode = new RSQLParser().parse("id==1");
        // not a recommended usage
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>(new Course());
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals("Testing Course", courses.get(0).getName());
    }


    @Test
    public void testNotEqualSelection() {
        Node rootNode = new RSQLParser().parse("id!=1");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals(0, courses.size());
    }

    @Test
    public void testGreaterThanSelection() {
        Node rootNode = new RSQLParser().parse("id=gt=1");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals(0, courses.size());
    }

    @Test
    public void testGreaterThanDate() {
        Node rootNode = new RSQLParser().parse("startDate=gt='2001-01-01'");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals(1, courses.size());
    }

    @Test
    public void testGreaterThanString() {
        Node rootNode = new RSQLParser().parse("code=gt='ABC'");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals(1, courses.size());
    }

    @Test
    public void testGreaterThanNotComparable() {
        try {
            Node rootNode = new RSQLParser().parse("details.teacher=gt='ABC'");
            RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
            rootNode.accept(visitor, entityManager);
            fail("should have failed since type isn't Comparable");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid type for comparison operator: =gt= type: com.github.tennaito.rsql.jpa.entity.Teacher must implement Comparable<Teacher>", e.getMessage());
        }
    }

    @Test
    public void testGreaterThanEqualSelection() {
        Node rootNode = new RSQLParser().parse("id=ge=1");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testGreaterThanEqualSelectionForDate() {
        Node rootNode = new RSQLParser().parse("startDate=ge='2016-01-01'");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testGreaterThanEqualSelectionForString() {
        Node rootNode = new RSQLParser().parse("code=ge='MI-MDW'");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testGreaterThanEqualNotComparable() {
        try {
            Node rootNode = new RSQLParser().parse("details.teacher=ge='ABC'");
            RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
            rootNode.accept(visitor, entityManager);
            fail("should have failed since type isn't Comparable");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid type for comparison operator: =ge= type: com.github.tennaito.rsql.jpa.entity.Teacher must implement Comparable<Teacher>", e.getMessage());
        }
    }

    @Test
    public void testLessThanSelection() {
        Node rootNode = new RSQLParser().parse("id=lt=1");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals(0, courses.size());
    }

    @Test
    public void testLessThanEqualSelection() {
        Node rootNode = new RSQLParser().parse("id=le=1");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testLessThanDate() {
        Node rootNode = new RSQLParser().parse("startDate=lt='2022-02-02'");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals(1, courses.size());
    }

    @Test
    public void testLessThanString() {
        Node rootNode = new RSQLParser().parse("code=lt='MI-MDZ'");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals(1, courses.size());
    }

    @Test
    public void testLessThanNotComparable() {
        try {
            Node rootNode = new RSQLParser().parse("details.teacher=lt='ABC'");
            RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
            rootNode.accept(visitor, entityManager);
            fail("should have failed since type isn't Comparable");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid type for comparison operator: =lt= type: com.github.tennaito.rsql.jpa.entity.Teacher must implement Comparable<Teacher>", e.getMessage());
        }
    }

    @Test
    public void testLessThanEqualSelectionForDate() {
        Node rootNode = new RSQLParser().parse("startDate=le='2100-01-01'");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testLessThanEqualSelectionForString() {
        Node rootNode = new RSQLParser().parse("code=le='MI-MDW'");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testLessThanEqualNotComparable() {
        try {
            Node rootNode = new RSQLParser().parse("details.teacher=le='ABC'");
            RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
            rootNode.accept(visitor, entityManager);
            fail("should have failed since type isn't Comparable");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid type for comparison operator: =le= type: com.github.tennaito.rsql.jpa.entity.Teacher must implement Comparable<Teacher>", e.getMessage());
        }
    }


    @Test
    public void testInSelection() {
        Node rootNode = new RSQLParser().parse("id=in=(1,2,3,4)");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testOutSelection() {
        Node rootNode = new RSQLParser().parse("id=out=(1,2,3,4)");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals(0, courses.size());
    }

    @Test
    public void testLikeSelection() {
        Node rootNode = new RSQLParser().parse("name==*Course");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testNotLikeSelection() {
        Node rootNode = new RSQLParser().parse("name!=*Course");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals(0, courses.size());
    }


    @Test
    public void testIsNullSelection() {
        Node rootNode = new RSQLParser().parse("name==null");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals(0, courses.size());
    }

    @Test
    public void testNotIsNullSelection() {
        Node rootNode = new RSQLParser().parse("name!=null");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testSetEntity() {
        Node rootNode = new RSQLParser().parse("id==1");
        RSQLVisitor<CriteriaQuery<?>, EntityManager> visitor = new JpaCriteriaQueryVisitor();
        ((JpaCriteriaQueryVisitor)visitor).setEntityClass(Course.class);
        CriteriaQuery<?> query = rootNode.accept(visitor, entityManager);
        List<Course> courses = (List<Course>)entityManager.createQuery(query).getResultList();
        assertEquals(1, courses.size());
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
        createDefOperator(visitor);

        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);
        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals("Testing Course", courses.get(0).getName());
    }

    private void createDefOperator(JpaCriteriaQueryVisitor<Course> visitor) {
        // define new operator resolver
        PredicateBuilderStrategy predicateStrategy = new PredicateBuilderStrategy() {
            @Override
            public <T> Predicate createPredicate(Node node, From root, Class<T> entity,
                    EntityManager manager, BuilderTools tools)
                            throws IllegalArgumentException {
                ComparisonNode comp = ((ComparisonNode)node);
                ComparisonNode def = new ComparisonNode(ComparisonOperatorProxy.EQUAL.getOperator(), comp.getSelector(), comp.getArguments());
                return PredicateBuilder.createPredicate(def, root, entity, manager, tools);
            }
        };
        visitor.getBuilderTools().setPredicateBuilder(predicateStrategy);
    }

    @Test
    public void testAssociationSelection() {
        Node rootNode = new RSQLParser().parse("department.id==1");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testAssociationAliasSelection() {
        Node rootNode = new RSQLParser().parse("dept.id==1");
        JpaCriteriaQueryVisitor<Course> visitor = new JpaCriteriaQueryVisitor<Course>();
        // add to SimpleMapper
        assertNotNull(((SimpleMapper)visitor.getBuilderTools().getPropertiesMapper()).getMapping());
        ((SimpleMapper)visitor.getBuilderTools().getPropertiesMapper()).addMapping(Course.class, new HashMap<String, String>());
        ((SimpleMapper)visitor.getBuilderTools().getPropertiesMapper()).addMapping(Course.class, "dept", "department");

        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);
        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals("Testing Course", courses.get(0).getName());

        ((SimpleMapper)visitor.getBuilderTools().getPropertiesMapper()).setMapping(null);
        assertNull(((SimpleMapper)visitor.getBuilderTools().getPropertiesMapper()).getMapping());
    }

    @Test
    public void testAssociationAliasSelectionWithAssociationAlias() {
        Node rootNode = new RSQLParser().parse("dept_id==1");
        JpaCriteriaQueryVisitor<Course> visitor = new JpaCriteriaQueryVisitor<Course>();
        // add to SimpleMapper
        assertNotNull(((SimpleMapper)visitor.getBuilderTools().getPropertiesMapper()).getMapping());
        ((SimpleMapper)visitor.getBuilderTools().getPropertiesMapper()).addMapping(Course.class, new HashMap<String, String>());
        ((SimpleMapper)visitor.getBuilderTools().getPropertiesMapper()).addMapping(Course.class, "dept_id", "department.id");

        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);
        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals("Testing Course", courses.get(0).getName());

        ((SimpleMapper)visitor.getBuilderTools().getPropertiesMapper()).setMapping(null);
        assertNull(((SimpleMapper)visitor.getBuilderTools().getPropertiesMapper()).getMapping());
    }

    @Test
    public void testAndSelection() {
        Node rootNode = new RSQLParser().parse("department.id==1;id==2");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals(0, courses.size());
    }

    @Test
    public void testBasicSelectionCount() {
        Node rootNode = new RSQLParser().parse("department.id==1");
        JpaCriteriaCountQueryVisitor<Course> visitor = new JpaCriteriaCountQueryVisitor<Course>();
        CriteriaQuery<Long> query = rootNode.accept(visitor, entityManager);

        Long courseCount = entityManager.createQuery(query).getSingleResult();
        assertEquals((Long)1L, courseCount);
        Root<Course> root = visitor.getRoot();
        assertNotNull(root);
        visitor.setRoot(root);
    }

    @Test
    public void testAndSelectionCount()  {
        Node rootNode = new RSQLParser().parse("department.id==1;id==2");
        RSQLVisitor<CriteriaQuery<Long>, EntityManager> visitor = new JpaCriteriaCountQueryVisitor<Course>();
        CriteriaQuery<Long> query = rootNode.accept(visitor, entityManager);

        Long courseCount = entityManager.createQuery(query).getSingleResult();
        assertEquals((Long)0L, courseCount);
    }

    @Test
    public void testOrSelection() {
        Node rootNode = new RSQLParser().parse("department.id==1,id==2");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testOrSelectionCount() {
        Node rootNode = new RSQLParser().parse("department.id==1,id==2");
        RSQLVisitor<CriteriaQuery<Long>, EntityManager> visitor = new JpaCriteriaCountQueryVisitor<Course>();
        CriteriaQuery<Long> query = rootNode.accept(visitor, entityManager);

        Long courseCount = entityManager.createQuery(query).getSingleResult();
        assertEquals((Long)1l, courseCount);
    }

    @Test
    public void testVariousNodesSelection() {
        Node rootNode = new RSQLParser().parse("((department.id==1;id==2),id<3);department.id=out=(3,4,5)");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testNavigateThroughCollectionSelection() {
        Node rootNode = new RSQLParser().parse("department.head.titles.name==Phd");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testUnsupportedNode() {
        try{
            PredicateBuilder.createPredicate(new OtherNode(), null, null, null, null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Unknown expression type: class com.github.tennaito.rsql.jpa.JpaVisitorTest$OtherNode", e.getMessage());
        }
    }

    @Test
    public void testSetBuilderTools() {
        JpaCriteriaQueryVisitor<Course> visitor = new JpaCriteriaQueryVisitor<Course>();
        visitor.setBuilderTools(null);
        assertNotNull(visitor.getBuilderTools());

        visitor.getBuilderTools().setArgumentParser(null);
        assertNotNull(visitor.getBuilderTools().getArgumentParser());

        visitor.getBuilderTools().setPropertiesMapper(null);
        assertNotNull(visitor.getBuilderTools().getPropertiesMapper());

        visitor.getBuilderTools().setPredicateBuilder(null);
        assertNull(visitor.getBuilderTools().getPredicateBuilder());
    }

    @Test
    public void testPrivateConstructor() throws Exception {
        Constructor<PredicateBuilder> priv = PredicateBuilder.class.getDeclaredConstructor();
        // It is really private?
        assertFalse(priv.isAccessible());
        priv.setAccessible(true);
        Object predicateBuilder = priv.newInstance();
        // When used it returns a instance?
        assertNotNull(predicateBuilder);
    }

    @Test
    public void testUndefinedRootForPredicate() {
        try {
            Node rootNode = new RSQLParser().parse("id==1");
            RSQLVisitor<Predicate, EntityManager> visitor = new JpaPredicateVisitor<Course>();
            Predicate query = rootNode.accept(visitor, entityManager);
        } catch (IllegalArgumentException e) {
            assertEquals("From root node was undefined.", e.getMessage());
        }
    }

    @Test
    public void testSelectionUsingEmbeddedField() {
        Node rootNode = new RSQLParser().parse("details.description==test");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals("Testing Course", courses.get(0).getName());
    }

    @Test
    public void testSelectionUsingEmbeddedAssociationField() {
        Node rootNode = new RSQLParser().parse("details.teacher.specialtyDescription==Maths");
        RSQLVisitor<CriteriaQuery<Course>, EntityManager> visitor = new JpaCriteriaQueryVisitor<Course>();
        CriteriaQuery<Course> query = rootNode.accept(visitor, entityManager);

        List<Course> courses = entityManager.createQuery(query).getResultList();
        assertEquals("Testing Course", courses.get(0).getName());
    }

    // Mock
    protected static class OtherNode extends AbstractNode {

        @Override
        public <R, A> R accept(RSQLVisitor<R, A> visitor, A param) {
            throw new UnsupportedOperationException();
        }
    }

}
