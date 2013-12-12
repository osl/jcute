package tests;

import cute.Cute;

//Written by Francois Rivest, 1997
//E-Mail: frives@po-box.mcgill.ca


//==============================================================================
// Binary Search Tree class      
//
//==============================================================================


public class BSTree
{
	int m_Key;
	BSTree m_Parent;
	BSTree m_LeftChild;
	BSTree m_RightChild;
	boolean m_Left;
	boolean m_Right;
	boolean m_this;

	//Constructs a node
	public BSTree(int nKey)
	{
		m_Key = nKey;
	}

	//Returns the current node value
	public int Key()
	{
		return m_Key;
	}

	//Returns the root of the tree
	public BSTree Root()
	{
		if (m_Parent == null) return this;
		else return m_Parent.Root();
	}

	//Inserts a node in the tree
	public void Insert(int nKey) 
	{
		if (nKey < m_Key) 
		{
			m_Left = true;
			if (m_LeftChild == null) 
			{
				m_LeftChild = new BSTree(nKey);
				m_LeftChild.m_Parent = this;	
				m_LeftChild.m_this = true;
			} 
			else 
			{
				m_LeftChild.Insert(nKey);
			}
		} 
		else 
		{
			m_Right = true;
			if (m_RightChild == null) 
			{
				m_RightChild = new BSTree(nKey);
				m_RightChild.m_Parent = this;
				m_RightChild.m_this = true;
			}
			else 
			{
				m_RightChild.Insert(nKey);
			}
		}
	}

	//Deletes a node q in from the tree
	public BSTree Delete(BSTree q)
	{
		int Count = 2, x;
		if (q.m_LeftChild == null) Count--;
		if (q.m_RightChild == null) Count--;

		switch (Count)
		{
			//No child
			case 0:
				if (q == this) return (new BSTree(0));
				if (q.m_Parent.m_LeftChild == q) q.m_Parent.m_LeftChild = null;
				else q.m_Parent.m_RightChild = null;
				break;
			//One child
			case 1:
				if (q.m_LeftChild != null)
				{
					q.m_LeftChild.m_Parent = q.m_Parent;
					q.m_LeftChild.m_this = true;
					if (q == this) return q.m_LeftChild;
					if (q.m_Parent.m_LeftChild == q)
					{
						q.m_Parent.m_LeftChild = q.m_LeftChild; 
					}
					else
					{
						q.m_Parent.m_RightChild = q.m_LeftChild;
					}
				}
				else
				{
					q.m_RightChild.m_Parent = q.m_Parent;
					q.m_RightChild.m_this = true;
					if (q == this) return q.m_RightChild;
					if (q.m_Parent.m_LeftChild == q)
					{
						q.m_Parent.m_LeftChild = q.m_RightChild; 
					}
					else
					{
						q.m_Parent.m_RightChild = q.m_RightChild;
					}
				}
				break;
			//2 children
			case 2:
				q.m_Right = true;
				x = q.m_RightChild.Min().m_Key;
				Delete(q.m_RightChild.Min());
				q.m_Key = x;
				q.m_this = true;
				break;
		}
		return this;
	}

	//Finds a node from the tree
	public BSTree Search(int nKey)
	{
		if (nKey == m_Key) 
		{
			m_this = true;
			return this;
		}
		else if (nKey < m_Key) 
		{
			m_Left = true;
			if (m_LeftChild != null) return m_LeftChild.Search(nKey);
			else return null;
		}
		else 
		{
			m_Right = true;
			if (m_RightChild != null) return m_RightChild.Search(nKey);
			else return null;
		}
	}

	//Returns the minimal node of the tree
	public BSTree Min()
	{
		if (m_LeftChild == null) 
		{
			m_this = true;
			return this;
		}
		else 
		{
			m_Left = true;
			return m_LeftChild.Min();
		}
	}

	//Returns the maximal node of the tree
	public BSTree Max()
	{
		if (m_RightChild == null) 
		{
			m_this = true;
			return this;
		}
		else 
		{
			m_Right = true;
			return m_RightChild.Max();
		}
	}

	//Cleans the path
	public void Clean()
	{
		m_Left = false;
		m_Right = false;
		m_this = false;
		if (m_LeftChild != null) m_LeftChild.Clean();
		if (m_RightChild != null) m_RightChild.Clean();
	}

    public static void main(String[] args) {
        BSTree b1 = new BSTree(Cute.input.Integer());
        for(int i=0;i<4;i++){
            switch(Cute.input.Integer()){
                case 0:
                    b1.Insert(Cute.input.Integer());
                    break;
                case 1:
                    b1.Search(Cute.input.Integer());
                    break;
                case 2:
                    b1.Delete((BSTree)Cute.input.Object("tests.BSTree"));
                    break;
                case 3:
                    b1.Root();
                    break;
                case 4:
                    b1.Clean();
                    break;
                case 5:
                    b1.Min();
                    break;
                default:
                    b1.Max();
                    break;
            }
        }
    }
}

//@The following comments are auto-generated to save options for testing the current file
//@jcute.optionPrintOutput=true
//@jcute.optionLogPath=false
//@jcute.optionLogTraceAndInput=false
//@jcute.optionGenerateJUnit=false
//@jcute.optionExtraOptions=
//@jcute.optionJUnitOutputFolderName=D:\sync\work\cute\java
//@jcute.optionJUnitPkgName=
//@jcute.optionNumberOfPaths=100
//@jcute.optionLogLevel=2
//@jcute.optionDepthForDFS=0
//@jcute.optionSearchStrategy=0
//@jcute.optionSequential=true
//@jcute.optionQuickSearchThreshold=100
//@jcute.optionLogRace=true
//@jcute.optionLogDeadlock=true
//@jcute.optionLogException=true
//@jcute.optionLogAssertion=true
//@jcute.optionUseRandomInputs=false
