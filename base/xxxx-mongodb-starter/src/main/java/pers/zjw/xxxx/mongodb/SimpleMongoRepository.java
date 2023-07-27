package pers.zjw.xxxx.mongodb;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * mongo repo
 *
 * @date 2022/03/31 0031 8:44
 * @author zhangjw
 */
@Repository
public class SimpleMongoRepository {
    @Autowired
    private MongoTemplate template;

    private Query emptyQuery = new Query();

    private int batchSize = 100;

    public MongoTemplate mongo() {
        return template;
    }

    public void createIndex(
            String colName, String fieldName, String idx, boolean unique) {
        Assert.hasLength(colName, "colName must not be null");
        Assert.hasLength(fieldName, "fieldName must not be null");
        Assert.hasLength(idx, "idx must not be null");
        if (!template.collectionExists(colName)) {
            template.createCollection(colName);
        }
        if (template.indexOps(colName).getIndexInfo().stream()
                .noneMatch(e -> e.getName().equals(idx))) {
            Index index = new Index().named(idx).on(fieldName, Sort.DEFAULT_DIRECTION);
            if (unique) {
                index.unique();
            }
            template.indexOps(colName).ensureIndex(index);
        }
    }

    public <T> void createIndex(
            Class<T> clazz, String fieldName, String idx, boolean unique) {
        Assert.notNull(clazz, "clazz must not be null");
        Assert.hasLength(fieldName, "fieldName must not be null");
        Assert.hasLength(idx, "idx must not be null");
        if (!template.collectionExists(clazz)) {
            template.createCollection(clazz);
        }
        if (template.indexOps(clazz).getIndexInfo().stream()
                .noneMatch(e -> e.getName().equals(idx))) {
            Index index = new Index().named(idx).on(fieldName, Sort.DEFAULT_DIRECTION);
            if (unique) {
                index.unique();
            }
            template.indexOps(clazz).ensureIndex(index);
        }
    }

    public <T> void insert(T entity) {
        Assert.notNull(entity, "entity must not be null");
        template.insert(entity);
    }

    public <T> void insert(T entity, String colName) {
        Assert.notNull(entity, "entity must not be null");
        Assert.hasText(colName, "colName must not be empty");
        template.insert(entity, colName);
    }

    public <T> void insertAll(List<T> data, String colName) {
        Assert.notNull(data, "data must not be null");
        Assert.hasLength(colName, "colName must not be empty");
        if (data.isEmpty()) return;
        if (data.size() <= batchSize) {
            template.insert(data, colName);
        } else {
            int start = 0;
            while (start < data.size()) {
                int end = Math.min(start + batchSize, data.size());
                template.insert(data.subList(start, end), colName);
                start = end;
            }
        }
    }

    public <T> void insertAll(List<T> data) {
        Assert.notNull(data, "data must not be null");
        if (data.isEmpty()) return;
        Class<?> clazz = data.get(0).getClass();
        if (data.size() <= batchSize) {
            template.insert(data, clazz);
        } else {
            int start = 0;
            while (start < data.size()) {
                int end = Math.min(start + batchSize, data.size());
                template.insert(data.subList(start, end), clazz);
                start = end;
            }
        }
    }

    public <T> void saveOrUpdate(T entity, String fieldName, Object fieldValue) {
        Assert.notNull(entity, "entity must not be null");
        Assert.hasText(fieldName, "fieldName must not be empty");
        Assert.hasText(fieldName, "fieldValue must not be empty");
        template.upsert(Query.query(Criteria.where(fieldName).is(fieldValue)),
                fromObject(entity), entity.getClass());
    }

    public <T> void saveOrUpdate(T entity, String fieldName, Object fieldValue, String colName) {
        Assert.notNull(entity, "entity must not be null");
        Assert.hasText(fieldName, "fieldName must not be empty");
        Assert.hasText(fieldName, "fieldValue must not be empty");
        Assert.hasText(colName, "colName must not be empty");
        template.upsert(Query.query(Criteria.where(fieldName).is(fieldValue)),
                fromObject(entity), colName);
    }

    public <T> void saveOrUpdate(T entity, Query query) {
        Assert.notNull(entity, "entity must not be null");
        Assert.notNull(query, "query must not be empty");
        template.upsert(query, fromObject(entity), entity.getClass());
    }

    public <T> void saveOrUpdate(T entity, Query query, String colName) {
        Assert.notNull(entity, "entity must not be null");
        Assert.notNull(query, "query must not be empty");
        Assert.hasText(colName, "colName must not be empty");
        template.upsert(query, fromObject(entity), colName);
    }

    public <T> void update(T entity, String fieldName, Object fieldValue) {
        Assert.notNull(entity, "entity must not be null");
        Assert.hasText(fieldName, "fieldName must not be empty");
        Assert.hasText(fieldName, "fieldValue must not be empty");
        template.updateFirst(Query.query(Criteria.where(fieldName).is(fieldValue)),
                fromObject(entity), entity.getClass());
    }

    public <T> void update(T entity, Query query) {
        update(entity, query, false);
    }

    public <T> void update(T entity, Query query, boolean multi) {
        Assert.notNull(entity, "entity must not be null");
        Assert.notNull(query, "query must not be empty");
        if (multi) {
            template.updateMulti(query, fromObject(entity), entity.getClass());
            return;
        }
        template.updateFirst(query, fromObject(entity), entity.getClass());
    }

    public <T> void update(T entity, Query query, String colName) {
        update(entity, query, colName, false);
    }

    public <T> void update(T entity, Query query, String colName, boolean multi) {
        Assert.notNull(entity, "entity must not be null");
        Assert.notNull(query, "query must not be empty");
        Assert.hasText(colName, "colName must not be empty");
        if (multi) {
            template.updateMulti(query, fromObject(entity), colName);
            return;
        }
        template.updateFirst(query, fromObject(entity), colName);
    }

    public void remove(String fieldName, Object fieldValue, Class<?> clazz) {
        Assert.notNull(clazz, "class must not be null");
        template.remove(Query.query(Criteria.where(fieldName).is(fieldValue)), clazz);
    }

    public void remove(String fieldName, Object fieldValue, String colName) {
        Assert.hasLength(colName, "colName must not be empty");
        template.remove(Query.query(Criteria.where(fieldName).is(fieldValue)), colName);
    }

    public void remove(Query query, Class<?> clazz) {
        Assert.notNull(clazz, "class must not be null");
        template.remove(query, clazz);
    }

    public void remove(Query query, String colName) {
        Assert.hasLength(colName, "colName must not be empty");
        template.remove(query, colName);
    }

    public void removeAll(Class<?> clazz) {
        Assert.notNull(clazz, "class must not be null");
        template.remove(emptyQuery, clazz);
    }

    public void removeAll(String colName) {
        Assert.hasLength(colName, "colName must not be empty");
        template.remove(emptyQuery, colName);
    }

    public <T> T findOne(String fieldName, Object fieldValue, Class<T> clazz, String colName) {
        Assert.hasText(fieldName, "fieldName must not be empty");
        Assert.hasText(fieldName, "fieldValue must not be empty");
        Assert.notNull(clazz, "class must not be null");
        Assert.hasLength(colName, "colName must not be empty");
        return template.findOne(Query.query(Criteria.where(fieldName).is(fieldValue)), clazz, colName);
    }

    public <T> T findOne(String fieldName, Object fieldValue, Class<T> clazz) {
        Assert.hasText(fieldName, "fieldName must not be empty");
        Assert.hasText(fieldName, "fieldValue must not be empty");
        Assert.notNull(clazz, "class must not be null");
        return template.findOne(Query.query(Criteria.where(fieldName).is(fieldValue)), clazz);
    }

    public <T> T findOne(Query query, Class<T> clazz) {
        Assert.notNull(clazz, "class must not be null");
        return template.findOne(query, clazz);
    }

    public <T> List<T> find(Query query, Class<T> clazz, String colName) {
        Assert.notNull(query, "query must not be null");
        Assert.notNull(clazz, "class must not be null");
        Assert.hasLength(colName, "colName must not be empty");
        return template.find(query, clazz, colName);
    }

    public <T> List<T> find(Query query, Class<T> clazz) {
        Assert.notNull(query, "query must not be null");
        Assert.notNull(clazz, "class must not be null");
        return template.find(query, clazz);
    }

    public <T> List<T> findAll(Class<T> clazz, String colName) {
        Assert.notNull(clazz, "class must not be null");
        Assert.hasLength(colName, "colName must not be empty");
        return template.findAll(clazz, colName);
    }

    public <T> List<T> findAll(Class<T> clazz) {
        Assert.notNull(clazz, "class must not be null");
        return template.findAll(clazz);
    }

    public Update fromObject(Object obj) {
        Assert.notNull(obj, "obj must not be null");
        Document doc = new Document();
        template.getConverter().write(obj, doc);
        Update update = new Update();
        doc.entrySet().stream().filter(e -> !ObjectUtils.isEmpty(e.getValue())).forEach(e -> update.set(e.getKey(), e.getValue()));
        return update;
        //return Update.fromDocument(doc);
    }
}
