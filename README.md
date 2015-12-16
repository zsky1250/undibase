undibase
========
1.NestedSet hierarchy结构的JPA实现
 该结构形如：(通过左右值 lft、rgt控制前序遍历，解决了Mysql数据库没有结构化查询语句带来的树结构存储问题)
 1A10
  |
  |--2B3
  |   |--4C5
  |   |--6D7
  |--8E9
  使用这种方式查询很快、但是更新一个节点就可能更改很多节点的左右值
  对应这种结构，给出了NestedSet的JPA实现。用法见例子

  1.1 在SpringDataJPA中 有两种使用方式
  a.针对某几个类单独使用：可以写一个接口集成中间接口 在给出中间接口的实现类
     public interface ICatalogDao extends JpaRepository<Catalog,Long>,ISpringDataJpaNestedTreeDao<Catalog,Long>
     public class ICatalogDaoImpl extends AbstractNestedSetRepository<Catalog,Long> implements ISpringDataJpaNestedTreeDao<Catalog,Long>
     其中AbstractNestedSetRepository是一个抽象类，已经写好了针对NestedSet形式的树操作方法。子类继承他在实现getDomianClass，返回具体的entity类型即可

  b.针对所有几个使用使用： b1.) 定义一个中间接口 制造供所有接口使用的baseRepository
                     b2.) 写一个工厂类xxxFactory，制造供所有接口使用的baseRepositoryImpl。在配置中加入factory-class="xxxFactory"  (SDJ 1.9之前)
                          或直接写baseRepositoryImpl 配置中加入repository-base-class="baseRepositoryImpl" (SDJ 1.9之后)

  源码结构说明
  ITreeDao -- 标记接口、树操作的根据口 定义了一些操作基本的方法
  ISpringDataJpaNestedTreeDao  --  标记接口、集成了ITreeDao 额外提供了一个getDomainClass方法
  AbstractNestedSetRepository -- 抽象类，继承了ISpringDataJpaNestedTreeDao。写了树操作的实际方法，子类只需集成getDomainClass。
                                 （针对a的情况使用，供具体的xxxImpl继承。）
  NestedSetRepository -- SpringDataJPA的中间接口（针对b的情况使用）
  NestedSetRepositoryImpl --  NestedSetRepository的实现类，作为repository-base-class（针对b的情况使用）
  UndiBaseRepositoryFactoryBean -- 工厂bean SDJ1.9之前 制造NestedSetRepositoryImpl使用。（针对b的情况使用）

zwr的项目脚手架
