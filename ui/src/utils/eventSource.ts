import { ACCESS_TOKEN } from '@/constant'


export async function createEventSource(
  { route, body, method = 'POST', abortController },
  { onMessage, onSuccess, onError, onAbort, onStart }: {
    onMessage: ({ data }: { data: string }) => void,
    onStart: () => void,
    onSuccess: () => void,
    onError: (err: any) => void,
    onAbort: () => void,
  }
) {
  onStart?.()

  const token = localStorage.getItem(ACCESS_TOKEN)
  const url = route
  const options = method === 'GET'
    ? {
      method,
      headers: {
        Authorization: token
      },
      signal: abortController?.signal
    }
    : {
      method,
      body: JSON.stringify(body),
      headers: {
        'content-type': 'application/json',
        Authorization: token
      },
      signal: abortController?.signal
    }

  const response = await fetch(url, options)
    .catch(err => {
      return err
    })

  const reader = response.body?.getReader();
  if (!reader) {
    onError('Empty Reader');
    return;
  }
  const decoder = new TextDecoder();
  const processStream = async () => {
    let buffer = '';
    while (true) {
      const { value, done } = await reader.read();
      if (done) {
        onSuccess();
        break;
      }

      buffer += decoder.decode(value, { stream: true });
      const parts = buffer.split('\n');
      buffer = parts.pop() || '';

      parts.forEach(part => {
        if (!part) return;
        const rawData = part.replace('data:', '');
        const data = JSON.parse(rawData);
        onMessage({ data: data.choices[0].delta })
      });
    }
    if (buffer) {
      const rawData = buffer.trim().replace('data:', '');
      const data = JSON.parse(rawData);
      onMessage({ data: data.choices[0].delta })
    }
  };

  processStream().catch((error) => {
    if (abortController?.signal?.aborted) {
      onAbort();
    } else {
      onError(error);
    }
  });

  return () => {
    abortController?.abort();
  };
}
