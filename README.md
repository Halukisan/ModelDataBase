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
Milvus是向量数据库模型构建，EsServiceImpl是Es的构建，存储清理后的训练数据，检查查询效率，MilvusServiceImpl中存放向量数据库的构建代码。MilvusIndexConstans中是向量数据库的结构设计。
### Milvus
向Milvus向量数据库存储大模型问答对信息，python存储设置HNSW索引，对问题描述构建向量，修改索引参数但提升效果并不明显。调研发现，在少量不足百万数据下，构建合适的分区可以提升查询速度。



### 问题
java代码构建Milvus向量数据库，jdk版本要高（大概17），低版本的jdk里面，不支持vector类型的向量。Es
