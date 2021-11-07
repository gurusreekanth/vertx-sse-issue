1. How to run the program?

    Please run the test - MessageConsumerClientTest#testMessageEgress(). This class bring up the SSE server and then
    client connects to the server. Once client connects server sents 10 msgs of size configured (by default 10kb).
    When each message arrives on client in some cases they got chunked, this is creating some issues in our SSE implementation.
    Basically we are unable to identify msg boundaries (where it is starting and where it is ending). If each message comes to client
    as a single chunk then they will have proper boundaries(1 chunk 1 msg).

Sample logs from local runs:


Nov 08, 2021 2:02:58 AM client.MessageConsumerClient handleServerSentEvent
INFO: Chunk size 1885
Nov 08, 2021 2:02:58 AM client.MessageConsumerClient handleServerSentEvent
INFO: Chunk size 8357
Nov 08, 2021 2:02:58 AM client.MessageConsumerClient handleServerSentEvent
INFO: Chunk size 10242
Nov 08, 2021 2:02:58 AM client.MessageConsumerClient handleServerSentEvent
INFO: Chunk size 10242
Nov 08, 2021 2:02:58 AM client.MessageConsumerClient handleServerSentEvent
INFO: Chunk size 3903
Nov 08, 2021 2:02:58 AM client.MessageConsumerClient handleServerSentEvent
INFO: Chunk size 6339
Nov 08, 2021 2:02:58 AM client.MessageConsumerClient handleServerSentEvent
INFO: Chunk size 10242
Nov 08, 2021 2:02:58 AM client.MessageConsumerClient handleServerSentEvent
INFO: Chunk size 10242
Nov 08, 2021 2:02:58 AM client.MessageConsumerClient handleServerSentEvent
INFO: Chunk size 10242
Nov 08, 2021 2:02:58 AM client.MessageConsumerClient handleServerSentEvent
INFO: Chunk size 10242
Nov 08, 2021 2:02:58 AM client.MessageConsumerClient handleServerSentEvent
INFO: Chunk size 10242
Nov 08, 2021 2:02:58 AM client.MessageConsumerClient handleServerSentEvent
INFO: Chunk size 7939
Nov 08, 2021 2:02:58 AM client.MessageConsumerClient handleServerSentEvent
INFO: Chunk size 2303
