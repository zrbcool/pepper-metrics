# 用户开发文档

## 1 fork Pepper-Metrics

Address: [GitHub: Pepper-Metrics](https://github.com/zrbcool/pepper-metrics/tree/dev)

## 2 如何与上游分支同步

### 2.1 为本地fork的分支配置上游

* 打开终端。
* 列出当前fork的远程仓库。

```bash
$ git remote -v
> origin  https://github.com/YOUR_USERNAME/YOUR_FORK.git (fetch)
> origin  https://github.com/YOUR_USERNAME/YOUR_FORK.git (push)
```

例如：
```bash
origin  https://github.com/Lord-X/pepper-metrics.git (fetch)
origin  https://github.com/Lord-X/pepper-metrics.git (push)

```

* 为当前的fork指定上游仓库

```bash
$ git remote add upstream https://github.com/ORIGINAL_OWNER/ORIGINAL_REPOSITORY.git
```

例如：

```bash
$ git remote add upstream https://github.com/zrbcool/pepper-metrics.git
```

* 最后验证

```bash
$ git remote -v
> origin    https://github.com/YOUR_USERNAME/YOUR_FORK.git (fetch)
> origin    https://github.com/YOUR_USERNAME/YOUR_FORK.git (push)
> upstream  https://github.com/ORIGINAL_OWNER/ORIGINAL_REPOSITORY.git (fetch)
> upstream  https://github.com/ORIGINAL_OWNER/ORIGINAL_REPOSITORY.git (push)
```

例如：

```bash
Lord_X_:pepper-metrics zhiminxu$ git remote -v
origin  https://github.com/Lord-X/pepper-metrics.git (fetch)
origin  https://github.com/Lord-X/pepper-metrics.git (push)
upstream        https://github.com/zrbcool/pepper-metrics.git (fetch)
upstream        https://github.com/zrbcool/pepper-metrics.git (push)
```

### 2.2 从上游同步代码到自己的fork

例如，要将上游的dev分支的update更新到本地的master分支中，通过以下步骤完成。

* 打开终端，并将当前工作目录更改为您的本地仓库。
* 从上游仓库获取分支及其各自的提交。 对 `master` 的提交将存储在本地分支 `upstream/master` 中。

```bash
$ git fetch upstream
> remote: Counting objects: 75, done.
> remote: Compressing objects: 100% (53/53), done.
> remote: Total 62 (delta 27), reused 44 (delta 9)
> Unpacking objects: 100% (62/62), done.
> From https://github.com/ORIGINAL_OWNER/ORIGINAL_REPOSITORY
>  * [new branch]      master     -> upstream/master
```

* 检出复刻的本地 master 分支。

```bash
$ git checkout master
> Switched to branch 'master'
```

* 将来自 `upstream/dev` 的更改合并到本地 `master` 分支中。 这会使复刻的 `master` 分支与上游仓库的dev分支同步，而不会丢失本地更改。

```bash
Lord_X_:pepper-metrics zhiminxu$ git merge upstream/dev
Merge made by the 'recursive' strategy.
 README.md          | 91 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++--
 docs/Dev_Guide.md  |  0
 docs/Dev_plan.md   |  2 +-
 docs/User_guide.md | 20 ++++++++++++--------
 4 files changed, 102 insertions(+), 11 deletions(-)
 create mode 100644 docs/Dev_Guide.md
```

## 3 提交PR

* 进入到自己的fork中，点击 `New pull request` 按钮。

![New pull request](http://image.feathers.top/image/New-pull-request.png)

* 选择要merge到的上游分支

![New-PR](http://image.feathers.top/image/New-PR.png)

* 填写comment后，点击create

![createPR](http://image.feathers.top/image/createPR.png)

* merge PR

![mergePR](http://image.feathers.top/image/mergePR.png)

* 结果

![PR结果](http://image.feathers.top/image/PR结果.png)