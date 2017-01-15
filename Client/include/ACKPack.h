#pragma once

#include "Packet.h"

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

					/// <summary>
					/// Created by Uzi the magnanimous, breaker of code and leader of IDES. He who has tamed the java beast and crossed the narrow C(++).
					/// on this, 1/12/2017 the day of reckoning.
					/// </summary>
					class ACKPack : public Packet
					{
					private:
						short blockNum = 0;

					public:
						ACKPack(short blockNum);

						virtual short getBlockNum();
					};

				}
			}
		}
	}
}
