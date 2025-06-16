import React from 'react';
import SyntaxHighlighter from 'react-syntax-highlighter';
import { a11yDark } from 'react-syntax-highlighter/dist/esm/styles/hljs'
import ReactMarkdown from 'react-markdown';
import remarkMath from 'remark-math';
import remarkGfm from 'remark-gfm';
import rehypeKatex from 'rehype-katex';
import rehypeRaw from 'rehype-raw';
import 'katex/dist/katex.min.css';
import 'github-markdown-css/github-markdown.css';
import './github-mardown-reset.css'
import styles from './index.less';
import copy from 'clipboard-copy';
import { CopyOutlined } from '@ant-design/icons';
import { message } from 'antd';

function replaceLatexDelimiters(str) {
  // 将\\[替换为$$
  str = str.replace(/\\\[/g, '$$$$');
  str = str.replace(/\\]/g, '$$$$');
  // 将\\(替换为$
  str = str.replace(/\\\(/g, '$');
  str = str.replace(/\\\)/g, '$');
  return str;
}

const PrettyMarkdown = ({ content, loading = false, withGithubStyle = false }) => {
  const processedContent = replaceLatexDelimiters(content);

  const handleCopy = (text) => {
    copy(text);
    message.success('复制成功');
  };

  const props = {}
  if (withGithubStyle) {
    props.className = "markdown-body";
  }

  return (
    <div
      {...props}
    >
      <ReactMarkdown
        remarkPlugins={[remarkMath, remarkGfm]}
        rehypePlugins={[
          rehypeRaw,
          [rehypeKatex, {
            strict: false,
            throwOnError: false,
            errorColor: "#ff0000",
            output: "htmlAndMathml"
          }]
        ]}
        children={processedContent}
        components={{
          code({ node, inline, className, children, ...props }) {
            const match = /language-(\w+)/.exec(className || '');
            return !inline && match ? (
              // PreTag="div"
              <div className={styles.codeWrapper}>
                <SyntaxHighlighter language={match[1]} style={a11yDark} showLineNumbers {...props} >
                  {String(children).replace(/\n$/, '')}
                </SyntaxHighlighter>

                {
                  !loading && <CopyOutlined className={styles.copyIcon} onClick={() => handleCopy(children)} />
                }
              </div>
            ) : (
              <code className={className} {...props}>
                {children}
              </code>
            );
          }
        }}
      >
      </ReactMarkdown>
    </div>
  );
};

export default PrettyMarkdown;