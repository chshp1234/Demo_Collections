.class public Lcom/csp/mynew/SmaliTest;
.super Ljava/lang/Object;
.source "SmaliTest.java"


# static fields
.field private static final TAG:Ljava/lang/String; = "MySmali"


# direct methods
.method public constructor <init>()V
    .registers 1

    .prologue
    .line 9
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method private static LogE(Ljava/lang/String;)V
    .registers 3
    .param p0, "msg"    # Ljava/lang/String;

    .prologue
    .line 73
    if-nez p0, :cond_a

    .line 74
    const-string v0, "MySmali"

    const-string v1, "null"

    invoke-static {v0, v1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 78
    :goto_9
    return-void

    .line 77
    :cond_a
    const-string v0, "MySmali"

    invoke-static {v0, p0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    goto :goto_9
.end method

.method public static getIntentKV(Landroid/content/Intent;)V
    .registers 8
    .param p0, "intent"    # Landroid/content/Intent;

    .prologue
    .line 48
    if-nez p0, :cond_8

    .line 49
    const-string v4, "intent is null"

    invoke-static {v4}, Lcom/csp/mynew/SmaliTest;->LogE(Ljava/lang/String;)V

    .line 70
    :goto_7
    return-void

    .line 54
    :cond_8
    :try_start_8
    const-string v4, "====================\u5f00\u59cb\u6253\u5370Intent===================="

    invoke-static {v4}, Lcom/csp/mynew/SmaliTest;->LogE(Ljava/lang/String;)V

    .line 55
    invoke-virtual {p0}, Landroid/content/Intent;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-static {v4}, Lcom/csp/mynew/SmaliTest;->LogE(Ljava/lang/String;)V

    .line 56
    invoke-virtual {p0}, Landroid/content/Intent;->getExtras()Landroid/os/Bundle;

    move-result-object v1

    .line 57
    .local v1, "extras":Landroid/os/Bundle;
    if-eqz v1, :cond_67

    .line 58
    invoke-virtual {v1}, Landroid/os/Bundle;->keySet()Ljava/util/Set;

    move-result-object v3

    .line 59
    .local v3, "keys":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/String;>;"
    if-eqz v3, :cond_67

    .line 60
    invoke-interface {v3}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    move-result-object v4

    :goto_24
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_67

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Ljava/lang/String;

    .line 61
    .local v2, "key":Ljava/lang/String;
    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    const-string v6, "key="

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    invoke-virtual {v5, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    const-string v6, "\t\tvalue="

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    invoke-virtual {v1, v2}, Landroid/os/Bundle;->get(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v6

    invoke-static {v6}, Lcom/csp/mynew/SmaliTest;->stringOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v6

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v5

    invoke-static {v5}, Lcom/csp/mynew/SmaliTest;->LogE(Ljava/lang/String;)V
    :try_end_58
    .catch Ljava/lang/Exception; {:try_start_8 .. :try_end_58} :catch_59
    .catchall {:try_start_8 .. :try_end_58} :catchall_6d

    goto :goto_24

    .line 65
    .end local v1    # "extras":Landroid/os/Bundle;
    .end local v2    # "key":Ljava/lang/String;
    .end local v3    # "keys":Ljava/util/Set;, "Ljava/util/Set<Ljava/lang/String;>;"
    :catch_59
    move-exception v0

    .line 66
    .local v0, "e":Ljava/lang/Exception;
    :try_start_5a
    invoke-virtual {v0}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move-result-object v4

    invoke-static {v4}, Lcom/csp/mynew/SmaliTest;->LogE(Ljava/lang/String;)V
    :try_end_61
    .catchall {:try_start_5a .. :try_end_61} :catchall_6d

    .line 68
    const-string v4, "====================\u7ed3\u675f\u6253\u5370Intent===================="

    invoke-static {v4}, Lcom/csp/mynew/SmaliTest;->LogE(Ljava/lang/String;)V

    goto :goto_7

    .end local v0    # "e":Ljava/lang/Exception;
    .restart local v1    # "extras":Landroid/os/Bundle;
    :cond_67
    const-string v4, "====================\u7ed3\u675f\u6253\u5370Intent===================="

    invoke-static {v4}, Lcom/csp/mynew/SmaliTest;->LogE(Ljava/lang/String;)V

    goto :goto_7

    .end local v1    # "extras":Landroid/os/Bundle;
    :catchall_6d
    move-exception v4

    const-string v5, "====================\u7ed3\u675f\u6253\u5370Intent===================="

    invoke-static {v5}, Lcom/csp/mynew/SmaliTest;->LogE(Ljava/lang/String;)V

    .line 69
    throw v4
.end method

.method public static printStack()V
    .registers 5

    .prologue
    .line 25
    invoke-static {}, Ljava/lang/Thread;->currentThread()Ljava/lang/Thread;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/Thread;->getStackTrace()[Ljava/lang/StackTraceElement;

    move-result-object v2

    .line 28
    .local v2, "stackTrace":[Ljava/lang/StackTraceElement;
    if-nez v2, :cond_b

    .line 45
    :goto_a
    return-void

    .line 31
    :cond_b
    const/4 v0, 0x0

    .line 32
    .local v0, "index":I
    :goto_c
    array-length v3, v2

    if-ge v0, v3, :cond_20

    .line 33
    const-string v3, "printStack"

    add-int/lit8 v1, v0, 0x1

    .end local v0    # "index":I
    .local v1, "index":I
    aget-object v4, v2, v0

    invoke-virtual {v4}, Ljava/lang/StackTraceElement;->getMethodName()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v3, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v3

    if-eqz v3, :cond_67

    move v0, v1

    .line 37
    .end local v1    # "index":I
    .restart local v0    # "index":I
    :cond_20
    const-string v3, " "

    invoke-static {v3}, Lcom/csp/mynew/SmaliTest;->LogE(Ljava/lang/String;)V

    .line 38
    const-string v3, "====================\u5f00\u59cb\u6253\u5370\u8c03\u7528\u6808===================="

    invoke-static {v3}, Lcom/csp/mynew/SmaliTest;->LogE(Ljava/lang/String;)V

    .line 39
    :goto_2a
    array-length v3, v2

    if-ge v0, v3, :cond_5c

    .line 40
    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    aget-object v4, v2, v0

    invoke-virtual {v4}, Ljava/lang/StackTraceElement;->getClassName()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    const-string v4, ": "

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    aget-object v4, v2, v0

    invoke-virtual {v4}, Ljava/lang/StackTraceElement;->getMethodName()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    const-string v4, "()"

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-static {v3}, Lcom/csp/mynew/SmaliTest;->LogE(Ljava/lang/String;)V

    .line 41
    add-int/lit8 v0, v0, 0x1

    goto :goto_2a

    .line 43
    :cond_5c
    const-string v3, "====================\u7ed3\u675f\u6253\u5370\u8c03\u7528\u6808===================="

    invoke-static {v3}, Lcom/csp/mynew/SmaliTest;->LogE(Ljava/lang/String;)V

    .line 44
    const-string v3, " "

    invoke-static {v3}, Lcom/csp/mynew/SmaliTest;->LogE(Ljava/lang/String;)V

    goto :goto_a

    .end local v0    # "index":I
    .restart local v1    # "index":I
    :cond_67
    move v0, v1

    .end local v1    # "index":I
    .restart local v0    # "index":I
    goto :goto_c
.end method

.method private static stringOf(Ljava/lang/Object;)Ljava/lang/String;
    .registers 2
    .param p0, "o"    # Ljava/lang/Object;

    .prologue
    .line 81
    invoke-static {p0}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method


# virtual methods
.method public log()V
    .registers 2

    .prologue
    .line 13
    const-string v0, ""

    .line 15
    .local v0, "sb":Ljava/lang/String;
    return-void
.end method
