# ModelDataBase
Es和向量数据库Milvus的构建与数据存储
### 数据存储与计算流程
1. **数据库创建**：构建所需Fields，以embedding为单索引（之后加入function calling后可以使用embedding向量和timestamp标量的双索引模式进行数据查找）
2. **Field配置**：异步构建索引，后台构建，便于其他操作......
3. **匹配数据**：使用正则表达式匹配txt文件所需的数据......
4. **处理数据**：使用IO流以单个文件为单位读取数据并加以处理......
5. **Embedding**:python调用大模型，提供计算接口，将question传入，以此计算向量值，存入embedding......
6. **插入数据**：以单个文件为单位插入向量数据库中以保证数据的一致性......

### 内容
Milvus是向量数据库模型构建，EsServiceImpl是Es的构建，存储清理后的训练数据，检查查询效率，MilvusServiceImpl中存放向量数据库的构建代码。MilvusIndexConstans中是向量数据库的结构设计。embedding中是调用的专门计算embedding的小模型，对外提供接口，供java后端调用。
### Milvus
向Milvus向量数据库存储大模型问答对信息，python存储设置HNSW索引，对问题描述构建向量，修改索引参数但提升效果并不明显。调研发现，在少量不足百万数据下，构建合适的分区可以提升查询速度。
### ElasticSearch
首先，设置了title和question作为联合查询，并以title、question、id作为索引，搜索的时候可以以此查询到更具体的数据。

5000条数据查询时间为60ms。

数据存储时使用分页存储，并将文件数据读入缓存，再批量插入ES中。
在实测中，插入数据所需时间变少，但分页存储会导致数据查询时间更久。

优化可参考https://zhuanlan.zhihu.com/p/99718374

### 调研模型缓存层
在资源有限和对实时性要求较高的场景下，加入缓存层可以有效降低推理部署成本、提升模型性能和效率。
核心模块：adapter、embedding、similarity和data_manager。adapter模块主要功能是处理各种任务的业务逻辑，并且能够将embedding、similarity、data_manager等模块串联起来；embedding模块主要负责将文本转换为语义向量表示，它将用户的查询转换为向量形式，并用于后续的召回或存储操作；
rank模块用于对召回的向量进行相似度排序和评估；data_manager模块主要用于管理数据库。
Cache可以存储先前的查询结果，如果模型对相同的查询再次提出，则可以直接从缓存中获取结果。


### 问题
java代码构建Milvus向量数据库，jdk版本要高（大概17），低版本的jdk里面，不支持vector类型的向量。Es


