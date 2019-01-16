
# bplusTree
make bplusTree by Java

## Code Summary 
- b+tree는 키에 의해서 각각 식별되는 레코드의 효율적인 삽입, 검색과 삭제를 통해 정렬된 데이터를 표현하기 위한 트리자료구조의 일종.
- 알고리즘은 크게 삽입, 삭제로 나뉘며 삽입에서는 split 작업이, 삭제에서는 merge와 redistribution(재분배) 작업이 필요. 
- 먼저 Node에 관한 생성자를 두 개 만듦. 하나는 non-leaf의 경우를 받고, 다른 하나는 leaf의 경우를 받음. 
  논리프의 경우에는 키 값만 필요하지만 리프 노드의 경우 키 값과 포인터가 모두 필요하기 때문에 경우를 나눔.
- Insert 함수는 재귀적으로 짰으며, 리프의 오버플로우가 발생하면 두 개의 노드로 분할하고 
  키 값들을 절반씩 분배해서 저장했으며, 분할된 왼쪽노드에서 제일 큰 키 값을 부모 인덱스 노드로 저장. 
- Delete함수도 재귀를 이용. underflow발생시 redistribution과 merge 두 경우가 있음. 
  redistribution은 인덱스 키 값을 조정하는 작업을 거쳤고, merge시는 인덱스 값을 삭제함.
  또한 루트일때와 루트가 아닐 때 경우를 나눔. 따라서 경우의 수가 많이 발생했고, 다양한 함수들을 통해 경우의 수를 처리함.

## How to run
- setting

    javac Node.java
    
    javac bptree.java
