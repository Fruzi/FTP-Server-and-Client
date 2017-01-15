#include "ACKPack.h"

namespace bgu
{
	namespace spl171
	{
		namespace net
		{
			namespace impl
			{
				namespace packets
				{

					ACKPack::ACKPack(short blockNum)
					{
						opCode = 4;
						this->blockNum = blockNum;
					}

					short ACKPack::getBlockNum()
					{
						return blockNum;
					}
				}
			}
		}
	}
}
